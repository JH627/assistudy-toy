package com.assistudy.shared.autoconfigure;

import com.assistudy.shared.exception.handler.ExceptionAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(ExceptionAdvice.class)
public class SharedWebAutoConfiguration {
}
