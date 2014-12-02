package camelinaction;

import org.apache.camel.builder.RouteBuilder;

public class ConstrutorRotas extends RouteBuilder {

	// torno configuravel as pastas de entrada e saida
	String origem = "data/inbox";
	String destino = "data/outbox";

	@Override
	public void configure() throws Exception {

		// Coloco a mensagem na fila correta
		from("file:" + origem + "?noop=true").process(new LogProcessor())
				.choice().when(header("CamelFileName").endsWith(".xml"))
				.to("jms:incomingOrdersXML")
				.when(header("CamelFileName").endsWith(".csv"))
				.to("jms:incomingOrdersCSV");

		// Retiro a mensagem da fila
		from("jms:incomingOrdersXML")
		// transformo para o padr�o desejado
				.bean(new Transormer(), "transformContent")
				// coloco a mensagem no destino final
				.to("file:" + destino + "?fileName=${file:name.noext}.CSV")
				// impe�o o roteamento futuro desta mensagem
				.end();
		
		// Retiro a mensagem da fila
		from("jms:incomingOrdersCSV")
				// coloco a mensagem no destino final
				.to("file:" + destino + "?fileName=${file:name.noext}.CSV")
				// impe�o o roteamento futuro desta mensagem
				.end();
	}

}