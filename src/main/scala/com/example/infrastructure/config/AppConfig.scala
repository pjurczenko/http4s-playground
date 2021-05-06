package com.example.infrastructure.config

import cats.effect.{Blocker, ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.typesafe.config.{Config, ConfigFactory}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

import scala.concurrent.duration.FiniteDuration

case class AppConfig(http: HttpConfig) {
  override def toString: String = s"AppConfig(http=$http)"
}

case class HttpConfig(server: HttpServerConfig) {
  override def toString: String = s"HttpConfig(server=$server)"
}

case class HttpServerConfig(host: String, port: Int, maxConnections: Int, timeout: FiniteDuration) {
  override def toString: String =
    s"HttpServerConfig(host=$host, port=$port, maxConnections=$maxConnections, timeout=$timeout)"
}

object AppConfig {

  def load[F[_]: Sync: ContextShift](blocker: Blocker): F[AppConfig] =
    for {
      logger <- Slf4jLogger.create
      env <- getEnv
      _ <- logger.info(s"Loading application config for $env env")
      configName = s"application-$env"
      _ <- validateConfigExists(configName)
      config <- getConfig(configName, blocker)
      appConfig <- getAppConfig(config, blocker)
      _ <- logger.info(s"Successfully loaded application config for $env env: $appConfig")
    } yield appConfig

  private def getEnv[F[_]: Sync]: F[String] =
    Sync[F].delay(Option(System.getenv("APP_ENV")).getOrElse("local"))

  private def getConfig[F[_]: Sync: ContextShift](configName: String, blocker: Blocker): F[Config] =
    blocker.delay(ConfigFactory.load(configName))

  private def getAppConfig[F[_]: Sync: ContextShift](config: Config, blocker: Blocker): F[AppConfig] =
    ConfigSource.fromConfig(config).loadF(blocker)

  private def validateConfigExists[F[_]: Sync](configName: String): F[Unit] = {
    val fileName = s"$configName.conf"

    Sync[F].delay(Option(Thread.currentThread.getContextClassLoader.getResource(fileName))).flatMap {
      case Some(_) => Sync[F].unit
      case None => Sync[F].raiseError(configNotFound(fileName))
    }
  }

  private def configNotFound(fileName: String): ConfigNotFoundException =
    ConfigNotFoundException(s"Configuration file not found on the classpath: $fileName")

}

private case class ConfigNotFoundException(message: String) extends Exception(message)
