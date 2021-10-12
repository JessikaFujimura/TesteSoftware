package com.fatec.testesoftware.REQ01;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class REQ01ManterLivroTest {

    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "browserDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://ts-scel-web.herokuapp.com/login");
        driver.manage().window().maximize();
       js = (JavascriptExecutor) driver;
       vars = new HashMap<String, Object>();
    }
    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void CT01CadastrarLivroComSucesso() {
        login();
        // dado que o livor não esteja cadastrado
        // e o usuário insere um ISBN válido, autor válido e título válido.
        driver.findElement(By.name("isbn")).click();
        driver.findElement(By.name("isbn")).sendKeys("1222");
        driver.findElement(By.name("autor")).click();
        driver.findElement(By.name("autor")).sendKeys("Robert C. Martin");
        driver.findElement(By.name("titulo")).click();
        driver.findElement(By.name("titulo")).sendKeys("Código Limpo");

        // quando o usuario cadastrar um livro
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();

        // entao o sistema redireciona pra a página de consulta com o livro cadastrado
        assertEquals("Código Limpo", driver.findElement(By.cssSelector("td:nth-child(3)")).getText());
        assertEquals(("Lista de livros"), driver.findElement(By.id("paginaConsulta")).getText());
        assertEquals("https://ts-scel-web.herokuapp.com/sig/livros", driver.getCurrentUrl());
        assertTrue(driver.getPageSource().contains("1222"));

//        mockDeletarLivro();
    }

    @Test
    public void CT02AtualizarLivroComSucesso() {
        // dado que o livro esta cadastrado
        login();
        mockCadastrarLivro();

        // quando o usuario altera o autor e título do livro
        driver.findElement(By.linkText("Editar")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(2)")).click();
        driver.findElement(By.id("autor")).clear();
        driver.findElement(By.id("autor")).sendKeys("Eric Evans");
        driver.findElement(By.id("titulo")).clear();
        driver.findElement(By.id("titulo")).sendKeys("Domain-Driven Design");
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();

        // entao o sistema apresenta as informações do livro com o titulo e autor alterados
        assertTrue(driver.getPageSource().contains("Domain-Driven Design"));
        assertTrue(driver.getPageSource().contains("Eric Evans"));

        mockDeletarLivro();
    }

    @Test
    public void CT03ExcluirLivroComSucesso() {
        login();
        mockCadastrarLivro();
        driver.findElement(By.linkText("Excluir")).click();

        assertFalse(driver.getPageSource().contains("Robert C. Martin"));
        assertFalse(driver.getPageSource().contains("Arquitetura Limpa"));
    }

    public void espera() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        driver.findElement(By.name("username")).click();
        driver.findElement(By.name("username")).sendKeys("jose");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button")).click();
        driver.findElement(By.linkText("Livros")).click();
        espera();
    }

    private void mockCadastrarLivro() {
        driver.findElement(By.name("isbn")).click();
        driver.findElement(By.name("isbn")).sendKeys("1122");
        driver.findElement(By.name("autor")).click();
        driver.findElement(By.name("autor")).sendKeys("Robert C. Martin");
        driver.findElement(By.name("titulo")).click();
        driver.findElement(By.name("titulo")).sendKeys("Arquitetura Limpa");
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();
    }

    private void mockDeletarLivro() {
        driver.findElement(By.linkText("Excluir")).click();
    }

}
