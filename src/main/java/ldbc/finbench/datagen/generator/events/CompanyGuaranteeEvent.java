package ldbc.finbench.datagen.generator.events;

import java.util.List;
import java.util.Random;
import ldbc.finbench.datagen.entities.edges.CompanyGuaranteeCompany;
import ldbc.finbench.datagen.entities.nodes.Company;
import ldbc.finbench.datagen.util.RandomGeneratorFarm;

public class CompanyGuaranteeEvent {
    private RandomGeneratorFarm randomFarm;
    private Random randIndex;
    private Random random;

    public CompanyGuaranteeEvent() {
        randomFarm = new RandomGeneratorFarm();
        randIndex = new Random();
        random = new Random();
    }

    public void companyGuarantee(List<Company> companies, int blockId) {
        random.setSeed(blockId);

        for (int i = 0; i < companies.size(); i++) {
            Company c = companies.get(i);
            int companyIndex = randIndex.nextInt(companies.size());

            if (guarantee()) {
                CompanyGuaranteeCompany.createCompanyGuaranteeCompany(
                        randomFarm.get(RandomGeneratorFarm.Aspect.DATE),
                        c,
                        companies.get(companyIndex));
            }
        }
    }

    private boolean guarantee() {
        //TODO determine whether to generate guarantee
        return true;
    }
}
