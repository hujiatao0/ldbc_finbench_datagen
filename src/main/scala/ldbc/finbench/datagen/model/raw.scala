package ldbc.finbench.datagen.model

import ldbc.finbench.datagen.model.Cardinality.{NN, NOne, OneN}
import ldbc.finbench.datagen.model.EntityType.{Edge, Node}

// define LDBC Finbench Data Schema
object raw {

  sealed trait RawEntity

  // define Person entity
  case class PersonRaw(
      id: Long,
      createTime: Long,
      name: String,
      isBlocked: Boolean
  ) extends RawEntity

  // define Account entity
  case class AccountRaw(
      id: Long,
      createTime: Long,
      deleteTime: Long,
      isBlocked: Boolean,
      `type`: String,
      inDegree: Long,
      OutDegree: Long,
      isExplicitDeleted: Boolean,
      Owner: String
  ) extends RawEntity

  // define Company entity
  case class CompanyRaw(
      id: Long,
      createTime: Long,
      name: String,
      isBlocked: Boolean
  ) extends RawEntity

  // define Loan entity
  case class LoanRaw(
      id: Long,
      loanAmount: Double,
      balance: Double
  ) extends RawEntity

  // define Medium entity
  case class MediumRaw(
      id: Long,
      createTime: Long,
      name: String,
      isBlocked: Boolean
  ) extends RawEntity

  // define PersonApplyLoan relationship
  case class PersonApplyLoanRaw(
      `personId`: Long,
      `loanId`: Long,
      loanAmount: Double,
      createTime: Long
  ) extends RawEntity

  // define CompanyApplyLoan relationship
  case class CompanyApplyLoanRaw(
      `companyId`: Long,
      `loanId`: Long,
      loanAmount: Double,
      createTime: Long
  ) extends RawEntity

  // define PersonInvestCompany relationship
  case class PersonInvestCompanyRaw(
      investorId: Long,
      companyId: Long,
      createTime: Long,
      ratio: Double
  ) extends RawEntity

  // define CompanyInvestCompany relationship
  case class CompanyInvestCompanyRaw(
      investorId: Long,
      companyId: Long,
      createTime: Long,
      ratio: Double
  ) extends RawEntity

  // define PersonGuaranteePerson relationship
  case class PersonGuaranteePersonRaw(
      from: Long,
      to: Long,
      createTime: Long
  ) extends RawEntity

  // define CompanyGuarantee relationship
  case class CompanyGuaranteeCompanyRaw(
      from: Long,
      to: Long,
      createTime: Long
  ) extends RawEntity

  //define PersonOwnAccount relationship
  case class PersonOwnAccountRaw(
      `personId`: Long,
      `accountId`: Long,
      createTime: Long,
      deleteTime: Long,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define CompanyOwnAccount relationship
  case class CompanyOwnAccountRaw(
      `companyId`: Long,
      `accountId`: Long,
      createTime: Long,
      deleteTime: Long,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define Transfer relationship
  case class TransferRaw(
      `fromId`: Long,
      `toId`: Long,
      multiplicityId: Long,
      createTime: Long,
      deleteTime: Long,
      amount: Double,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define Withdraw relationship
  case class WithdrawRaw(
      `account1Id`: Long,
      `account2Id`: Long,
      createTime: Long,
      amount: Double,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define Repay relationship
  case class RepayRaw(
      `accountId`: Long,
      `loanId`: Long,
      createTime: Long,
      amount: Double,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define Deposit relationship
  case class DepositRaw(
      `loanId`: Long,
      `accountId`: Long,
      createTime: Long,
      amount: Double,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  // define SignIn relationship
  case class SignInRaw(
      `mediumId`: Long,
      `accountId`: Long,
      multiplicityId: Long,
      createTime: Long,
      deleteTime: Long,
      isExplicitDeleted: Boolean
  ) extends RawEntity

  val PersonType = Node("Person")
  val CompanyType = Node("Company")
  val AccountType = Node("Account")
  val LoanType = Node("LoanType")
  val MediumType = Node("Medium")

  val CompanyApplyLoanType = Edge("Apply", CompanyType, LoanType, OneN)
  val CompanyGuaranteeCompanyType = Edge("Guarantee", CompanyType, CompanyType, NN)
  val CompanyInvestCompanyType = Edge("Invest", CompanyType, CompanyType, NN)
  val CompanyOwnAccountType = Edge("Own", CompanyType, AccountType, OneN)
  val PersonApplyLoanType = Edge("Apply", PersonType, LoanType, OneN)
  val PersonGuaranteePersonType = Edge("Guarantee", PersonType, PersonType, NN)
  val PersonInvestCompanyType = Edge("Invest", PersonType, CompanyType, OneN)
  val PersonOwnAccountType = Edge("Own", PersonType, AccountType, OneN)
  val DepositType = Edge("Deposit", LoanType, AccountType, NN)
  val RepayType = Edge("Repay", AccountType, LoanType, NN)
  val SignInType = Edge("SignIn", MediumType, AccountType, OneN)
  val TransferType = Edge("Transfer", AccountType, AccountType, NN)
  val WithdrawType = Edge("Withdraw", AccountType, AccountType, NN)
  val WorkInType = Edge("WorkIn", PersonType, CompanyType, NOne)

  trait EntityTraitsInstances {
    import EntityTraits._
    import ldbc.finbench.datagen.util.Sql._

    implicit val entityTraitsForPerson: EntityTraits[PersonRaw] = pure(PersonType,1.0)
    implicit val entityTraitsForCompany: EntityTraits[CompanyRaw] = pure(CompanyType, 1.0)
    implicit val entityTraitsForAccount: EntityTraits[AccountRaw] = pure(AccountType, 1.0)
    implicit val entityTraitsForLoan: EntityTraits[LoanRaw] = pure(LoanType, 1.0)
    implicit val entityTraitsForMedium: EntityTraits[MediumRaw] = pure(MediumType, 1.0)

    implicit val entityTraitsForCompanyApplyLoan: EntityTraits[CompanyApplyLoanRaw] = pure(CompanyApplyLoanType, 1.0)
    implicit val entityTraitsForCompanyGuaranteeCompany: EntityTraits[CompanyGuaranteeCompanyRaw] = pure(CompanyGuaranteeCompanyType, 1.0)
    implicit val entityTraitsForCompanyInvestCompany: EntityTraits[CompanyInvestCompanyRaw] = pure(CompanyInvestCompanyType, 1.0)
    implicit val entityTraitsForCompanyOwnAccount: EntityTraits[CompanyOwnAccountRaw] = pure(CompanyOwnAccountType, 1.0)
    implicit val entityTraitsForPersonApplyLoan: EntityTraits[PersonApplyLoanRaw] = pure(PersonApplyLoanType, 1.0)
    implicit val entityTraitsForPersonGuaranteePerson: EntityTraits[PersonGuaranteePersonRaw] = pure(PersonGuaranteePersonType, 1.0)
    implicit val entityTraitsForPersonInvestCompany: EntityTraits[PersonInvestCompanyRaw] = pure(PersonInvestCompanyType, 1.0)
    implicit val entityTraitsForPersonOwnAccount: EntityTraits[PersonOwnAccountRaw] = pure(PersonOwnAccountType, 1.0)
    implicit val entityTraitsForDeposit: EntityTraits[DepositRaw] = pure(DepositType, 1.0)
    implicit val entityTraitsForRepay: EntityTraits[RepayRaw] = pure(RepayType, 1.0)
    implicit val entityTraitsForSignIn: EntityTraits[SignInRaw] = pure(SignInType, 1.0)
    implicit val entityTraitsForTransfer: EntityTraits[TransferRaw] = pure(TransferType, 1.0)
    implicit val entityTraitsForWithdraw: EntityTraits[WithdrawRaw] = pure(WithdrawType, 1.0)
//    implicit val entityTraitsWorkIn: EntityTraits[WorkInRaw] = pure(WorkInType, 1.0)
  }

  object instances extends EntityTraitsInstances
}
