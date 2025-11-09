package com.Livro.Caixa.controller;

import com.Livro.Caixa.model.Produto;
import com.Livro.Caixa.service.ProdutoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Value("${fabrico.upload.dir:./data/uploads}")
    private String uploadDir;

    private final ProdutoService service;
    private final Logger log = LoggerFactory.getLogger(ProdutoController.class);

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // Listar produtos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", service.listarAtivos());
        return "produtos/lista"; // Thymeleaf template
    }

    // Formulário de novo produto
    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form"; // Thymeleaf template
    }

    // Criar produto com upload de foto
    @PostMapping
    public String criar(@Valid @ModelAttribute Produto produto,
                        BindingResult br,
                        @RequestParam("foto") MultipartFile foto) throws IOException {
        if (br.hasErrors()) {
            return "produtos/form";
        }

        if (!foto.isEmpty()) {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "-" + foto.getOriginalFilename();
            Path dest = dir.resolve(filename);
            Files.copy(foto.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            produto.setFotoPath(dest.toString());
        }

        service.salvar(produto);
        log.info("Cadastro de produto realizado: {}", produto.getNome());
        return "redirect:/produtos";
    }

    // Deletar produto (somente ADMIN)
    @PostMapping("/{id}/deletar")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletar(@PathVariable Long id) {
        service.deletar(id);
        return "redirect:/produtos";
    }
}