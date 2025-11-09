/*------------------------------------------::
:: Author: Diogo Santos Pombo - \Õ/ - @2025 ::
::------------------------------------------*/

package com.Livro.Caixa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}