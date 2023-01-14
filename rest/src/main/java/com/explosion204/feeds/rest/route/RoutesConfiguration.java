package com.explosion204.feeds.rest.route;

import com.explosion204.feeds.rest.route.handlers.CommentHandler;
import com.explosion204.feeds.rest.route.handlers.PostHandler;
import com.explosion204.feeds.rest.route.handlers.PostVoteHandler;
import com.explosion204.feeds.rest.route.handlers.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class RoutesConfiguration {
  @Bean
  public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
    return RouterFunctions.route(GET("/users/{id}"), userHandler::findById)
            .andRoute(POST("/users"), userHandler::create)
            .andRoute(PUT("/users/{id}"), userHandler::update)
            .andRoute(DELETE("/users/{id}"), userHandler::delete);
  }

  @Bean
  public RouterFunction<ServerResponse> postRoutes(PostHandler postHandler) {
    return RouterFunctions.route(GET("/posts/{id}"), postHandler::findById)
            .andRoute(POST("/posts"), postHandler::create)
            .andRoute(PUT("/posts/{id}"), postHandler::update)
            .andRoute(DELETE("/posts/{id}"), postHandler::delete);
  }

  @Bean
  public RouterFunction<ServerResponse> commentRoutes(CommentHandler commentHandler) {
    return RouterFunctions.route(GET("/comments/{id}"), commentHandler::findById)
            .andRoute(POST("/comments"), commentHandler::create)
            .andRoute(DELETE("/comments/{id}"), commentHandler::delete);
  }

  @Bean
  public RouterFunction<ServerResponse> postVoteRoutes(PostVoteHandler postVoteHandler) {
    return RouterFunctions.route(GET("/post_votes/{user_id}/{post_id}"), postVoteHandler::findById)
            .andRoute(POST("/post_votes"), postVoteHandler::create)
            .andRoute(PUT("/post_votes/{user_id}/{post_id}"), postVoteHandler::update)
            .andRoute(DELETE("/post_votes/{user_id}/{post_id}"), postVoteHandler::delete);
  }
}
