package ldbc.finbench.datagen.entities.edges;

import java.io.Serializable;
import ldbc.finbench.datagen.entities.DynamicActivity;
import ldbc.finbench.datagen.entities.nodes.Account;
import ldbc.finbench.datagen.entities.nodes.Person;
import ldbc.finbench.datagen.entities.nodes.PersonOrCompany;
import ldbc.finbench.datagen.generation.dictionary.Dictionaries;
import ldbc.finbench.datagen.util.RandomGeneratorFarm;

public class PersonOwnAccount implements DynamicActivity, Serializable {
    private final long personId;
    private final long accountId;
    private final Account account; // TODO: can be removed
    private final long creationDate;
    private final long deletionDate;
    private final boolean isExplicitlyDeleted;
    private final String comment;

    public PersonOwnAccount(Person person, Account account, long creationDate, long deletionDate,
                            boolean isExplicitlyDeleted, String comment) {
        this.personId = person.getPersonId();
        this.accountId = account.getAccountId();
        this.account = account; // TODO: can be removed
        this.creationDate = creationDate;
        this.deletionDate = deletionDate;
        this.isExplicitlyDeleted = isExplicitlyDeleted;
        this.comment = comment;
    }

    public static void createPersonOwnAccount(RandomGeneratorFarm farm, Person person, Account account,
                                              long creationDate) {
        // Delete when account is deleted
        account.setOwnerType(PersonOrCompany.PERSON);
        account.setPersonOwner(person);
        String comment =
            Dictionaries.randomTexts.getUniformDistRandomTextForComments(
                farm.get(RandomGeneratorFarm.Aspect.COMMON_COMMENT));
        PersonOwnAccount personOwnAccount = new PersonOwnAccount(person, account, creationDate,
                                                                 account.getDeletionDate(),
                                                                 account.isExplicitlyDeleted(),
                                                                 comment);
        person.getPersonOwnAccounts().add(personOwnAccount);
    }

    public long getPersonId() {
        return personId;
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public long getCreationDate() {
        return creationDate;
    }

    @Override
    public long getDeletionDate() {
        return deletionDate;
    }

    @Override
    public boolean isExplicitlyDeleted() {
        return isExplicitlyDeleted;
    }

    public String getComment() {
        return comment;
    }

    public Account getAccount() {
        return account;
    }
}
