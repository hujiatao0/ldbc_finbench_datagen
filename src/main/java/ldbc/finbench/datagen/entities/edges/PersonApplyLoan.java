package ldbc.finbench.datagen.entities.edges;

import java.io.Serializable;
import java.util.Random;
import ldbc.finbench.datagen.entities.DynamicActivity;
import ldbc.finbench.datagen.entities.nodes.Loan;
import ldbc.finbench.datagen.entities.nodes.Person;
import ldbc.finbench.datagen.generator.dictionary.Dictionaries;

public class PersonApplyLoan implements DynamicActivity, Serializable {
    private long personId;
    private long loanId;
    private long creationDate;
    private long deletionDate;
    private boolean isExplicitlyDeleted;

    public PersonApplyLoan(long personId, long loanId,
                           long creationDate, long deletionDate, boolean isExplicitlyDeleted) {
        this.personId = personId;
        this.loanId = loanId;
        this.creationDate = creationDate;
        this.deletionDate = deletionDate;
        this.isExplicitlyDeleted = isExplicitlyDeleted;
    }

    public static PersonApplyLoan createPersonApplyLoan(Random random, Person person, Loan loan) {
        long creationDate = Dictionaries.dates.randomPersonToLoanDate(random, person, loan);

        PersonApplyLoan personApplyLoan = new PersonApplyLoan(person.getPersonId(), loan.getLoanId(),
                creationDate, 0, false);
        person.getPersonApplyLoans().add(personApplyLoan);

        return personApplyLoan;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    @Override
    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public long getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(long deletionDate) {
        this.deletionDate = deletionDate;
    }

    @Override
    public boolean isExplicitlyDeleted() {
        return isExplicitlyDeleted;
    }

    public void setExplicitlyDeleted(boolean explicitlyDeleted) {
        isExplicitlyDeleted = explicitlyDeleted;
    }
}