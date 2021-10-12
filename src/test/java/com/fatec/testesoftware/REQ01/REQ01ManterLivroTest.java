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
       login();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void CT01CadastrarLivroComSucesso() {
        // o livro com o ISBN 1001, autor Robert Martin e título Código Limpo não está cadastrado
        // quando usuário solicitar o cadastro do livro
        mockCadastrarLivro("1001", "Robert Martin", "Código Limpo");

        // entao o sistema redireciona pra a página de consulta com o livro cadastrado
        assertEquals("Código Limpo", driver.findElement(By.cssSelector("td:nth-child(3)")).getText());
        assertEquals(("Lista de livros"), driver.findElement(By.id("paginaConsulta")).getText());
        assertEquals("https://ts-scel-web.herokuapp.com/sig/livros", driver.getCurrentUrl());
        assertTrue(driver.getPageSource().contains("1001"));

        mockDeletarLivro();
    }

    @Test
    public void CT02CadastrarLivroISBNCadastrado() {
        // dado que o livro não esteja cadastrado
        // e o usuário insere um ISBN inválido, autor válido e título válido.
        mockCadastrarLivro("1001", "Robert Martin", "Código Limpo");
        driver.findElement(By.linkText("Voltar")).click();
        driver.findElement(By.linkText("Livros")).click();
        // quando o usuario cadastrar um livro
        mockCadastrarLivro("1001", "Robert Martin", "Código Limpo");

        // entao o sistema não cadastra o livro e mostra a mensagem de ISBN deve ter 4 caracteres
        assertEquals("Livro ja cadastrado", driver.findElement(By.className("text-danger")).getText());

        driver.findElement(By.cssSelector(".btn:nth-child(2)")).click();
        mockDeletarLivro();
    }

    @Test
    public void CT03CadastrarLivroComISBNInvalido() {
        // dado que o livro não esteja cadastrado
        // e o usuário insere um ISBN invalido, autor válido e título válido.
        // quando o usuario cadastrar um livro
        mockCadastrarLivro("999", "Robert Martin", "Código Limpo");

        // entao o sistema não cadastra o livro e mostra a mensagem de ISBN deve ter 4 caracteres
        assertEquals("ISBN deve ter 4 caracteres", driver.findElement(By.className("text-danger")).getText());
    }

    @Test
    public void CT04CadastrarLivroComAutorInválido() {
        // dado que o livro não esteja cadastrado
        // e o usuário insere um ISBN válido, autor inválido e título válido.
        // quando o usuario cadastrar um livro
        mockCadastrarLivro("9999", "Joaquim Maria Machado de Oliveira da  Silva Pereira Silva Pereira Silva Pereira Silva Pereira", "Dom Casmurro");

        // entao o sistema não cadastra o livro e mostra a mensagem de ISBN deve ter 4 caracteres
        assertEquals("Autor deve ter entre 1 e 50 caracteres", driver.findElement(By.className("text-danger")).getText());
    }


    @Test
    public void CT04CadastrarLivroComTituloInválido() {
        // dado que o livro não esteja cadastrado
        // e o usuário insere um ISBN válido, autor válido e título inválido.
        // quando o usuario cadastrar um livro
        mockCadastrarLivro("9999", "Evandro Affonso ", "O mendigo que sabia de cor os adágios de Erasmo de Rotterdam");

        // entao o sistema não cadastra o livro e mostra a mensagem de ISBN deve ter 4 caracteres
        assertEquals("Titulo deve ter entre 1 e 50 caracteres", driver.findElement(By.className("text-danger")).getText());
    }

    @Test
    public void CT06ConsultarLivroComSucesso() {
        // dado que o livro esteja cadastrado
        mockCadastrarLivro("1234", "Robert C. Martin", "Arquitetura Limpa");
        driver.findElement(By.linkText("Voltar")).click();
        driver.findElement(By.linkText("Livros")).click();
        driver.findElement(By.name("isbn")).click();

        // quando o usuario informar um ISBN cadastrado
        driver.findElement(By.name("isbn")).sendKeys("1234");
        driver.findElement(By.cssSelector(".btn:nth-child(2)")).click();

        // entao o sistema mostra uma listagem de livro cadastrado
        assertEquals(("Lista de livros"), driver.findElement(By.id("paginaConsulta")).getText());
        assertTrue(driver.getPageSource().contains("1234"));
        assertTrue(driver.getPageSource().contains("Robert C. Martin"));

        mockDeletarLivro();
    }

    @Test
    public void CT07AtualizarLivroComSucesso() {
        // dado que o livro esta cadastrado
        mockCadastrarLivro("1234", "Robert C. Martin", "Arquitetura Limpa");

        espera();
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
    public void CT08ExcluirLivroComSucesso() {
        // dado que o livro esta cadastrado
        mockCadastrarLivro("1234", "Robert C. Martin", "Arquitetura Limpa");

        // quando o usuario excluir um livro
        driver.findElement(By.linkText("Excluir")).click();

        // entao o sistema remove o livro da listagem de livros cadastrados
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

    private void mockCadastrarLivro(String isbn, String autor, String titulo) {
        driver.findElement(By.name("isbn")).click();
        driver.findElement(By.name("isbn")).sendKeys(isbn);
        driver.findElement(By.name("autor")).click();
        driver.findElement(By.name("autor")).sendKeys(autor);
        driver.findElement(By.name("titulo")).click();
        driver.findElement(By.name("titulo")).sendKeys(titulo);
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();
    }

    private void mockDeletarLivro() {
        driver.findElement(By.linkText("Excluir")).click();
    }

}
