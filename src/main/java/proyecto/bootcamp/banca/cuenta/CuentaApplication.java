package proyecto.bootcamp.banca.cuenta;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import proyecto.bootcamp.banca.cuenta.model.*;
import proyecto.bootcamp.banca.cuenta.repository.AccountTypeRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientAccountRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientTypeRepository;

@SpringBootApplication
public class CuentaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuentaApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(AccountTypeRepository accountRepository
			, ClientTypeRepository repository
			, ClientRepository clientRepository
			, ClientAccountRepository clienteAccountRepository){
		return args->{

////			Exclusivos de Clientes
//			AccountConditions personal = new AccountConditions(2,1,1,1,true);
//			AccountConditions empresarial= new AccountConditions(-1,0,-1,0,false);
//			CreditConditions cpersonal= new CreditConditions(1);
//			CreditConditions cempresarial= new CreditConditions(-1);
//
//			ClientType clientTypePersonal= new ClientType("Personal",personal,cpersonal);
//			ClientType clientTypeEmpresarial= new ClientType("Empresarial",empresarial,cempresarial);
//			repository.insert(clientTypePersonal);
//			repository.insert(clientTypeEmpresarial);
//
//			Client clientePersonal=new Client("43232343","DNI","Victor Dennis",clientTypePersonal);
//			Client clienteEmpresarial=new Client("20143232343","RUC","JAvaDus Company",clientTypeEmpresarial);
//			clientRepository.insert(clientePersonal);
//			clientRepository.insert(clienteEmpresarial);
//
////			Exclusivos para cuenta
//			Conditions ahorros= new Conditions(0,30,0,"-","-");
//			Conditions corriente= new Conditions(10,-1,0,"1+","0+");
//			Conditions pfijo= new Conditions(0,1,15,"-","-");
//
//			AccountType cAhorros= new AccountType("Ahorros",ahorros);
//			AccountType cCorriente= new AccountType("Corriente",corriente);
//			AccountType cPFijo= new AccountType("Plazo Fijo",pfijo);
//
//			accountRepository.insert(cAhorros);
//			accountRepository.insert(cCorriente);
//			accountRepository.insert(cPFijo);
//
//			ClientAccount ca1 = new ClientAccount("0011-121232-000232123",cAhorros,clientePersonal,null,0.0);
//			ClientAccount ca2 = new ClientAccount("0011-989898-000232123",cCorriente,clienteEmpresarial,null,0.0);
//			clienteAccountRepository.insert(ca1);
//			clienteAccountRepository.insert(ca2);
//			System.out.println("ejecuté repositorio");
		};
	}

}
