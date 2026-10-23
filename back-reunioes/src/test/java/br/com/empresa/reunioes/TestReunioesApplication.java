package br.com.empresa.reunioes;

import org.springframework.boot.SpringApplication;

public class TestReunioesApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReunioesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
