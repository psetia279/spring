package rewardsonline.accounts;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import common.money.Percentage;

/**
 * An account for a member of the reward network. An account has one or more
 * beneficiaries whose allocations must add up to 100%. An aggregate entity.
 *<p>
 * An account can make contributions to its beneficiaries. Each contribution is
 * distributed among the beneficiaries based on an allocation.
 */
@Entity
@Table(name = "T_ACCOUNT")
public class Account {

	@Id
	@Column(name = "ID")
	@GeneratedValue
	private Integer entityId;

	@NotEmpty
	private String number;

	private String name;

	@Column(name = "CREDIT_CARD")
	private String creditCardNumber;

	@Column(name = "DATE_OF_BIRTH")
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth;

	@Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
	private String email;

	@Column(name = "REWARDS_NEWSLETTER")
	private boolean receiveNewsletter;

	@Column(name = "MONTHLY_EMAIL_UPDATE")
	private boolean receiveMonthlyEmailUpdate;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "ACCOUNT_ID")
	private Set<Beneficiary> beneficiaries = new LinkedHashSet<Beneficiary>();

	/**
	 * Required by JPA and Jackson.
	 */
	protected Account() {
	}

	/**
	 * Create a new account.
	 * 
	 * @param number
	 *            the account number
	 * @param name
	 *            the name on the account
	 */
	public Account(String number, String name) {
		this.number = number;
		this.name = name;
	}

	/**
	 * Returns the entity identifier used to internally distinguish this entity
	 * among other entities of the same type in the system. Should typically
	 * only be called by privileged data access infrastructure code such as an
	 * Object Relational Mapper (ORM) and not by application code.
	 * 
	 * @return the internal entity identifier
	 */
	public Integer getEntityId() {
		return entityId;
	}

	/**
	 * Sets the internal entity identifier - should only be called by privileged
	 * data access code (repositories that work with an Object Relational Mapper
	 * (ORM)). Should never be set by application code explicitly.
	 * 
	 * @param entityId
	 *            the internal entity identifier
	 */
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	/**
	 * Returns the number used to uniquely identify this account.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the number used to uniquely identify this account.
	 * 
	 * @param number
	 *            The number for this account
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Returns the name on file for this account.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name on file for this account.
	 * 
	 * @param name
	 *            The name for this account
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Removes a single beneficiary from this account.
	 * 
	 * @param beneficiaryName
	 *            the name of the beneficiary (should be unique)
	 */
	public void removeBeneficiary(String beneficiaryName) {
		beneficiaries.remove(getBeneficiary(beneficiaryName));
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isReceiveNewsletter() {
		return receiveNewsletter;// == 'Y';
	}

	public void setReceiveNewsletter(boolean receiveNewsletter) {
		this.receiveNewsletter = receiveNewsletter;// ? 'Y' : 'N';
	}

	public boolean isReceiveMonthlyEmailUpdate() {
		return receiveMonthlyEmailUpdate;  // == 'Y';
	}

	public void setReceiveMonthlyEmailUpdate(boolean receiveMonthlyEmailUpdate) {
		this.receiveMonthlyEmailUpdate = receiveMonthlyEmailUpdate; // ? 'Y' : 'N';
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	/**
	 * Add a single beneficiary with a 100% allocation percentage.
	 * 
	 * @param beneficiaryName
	 *            the name of the beneficiary (should be unique)
	 */
	public void addBeneficiary(String beneficiaryName) {
		addBeneficiary(beneficiaryName, Percentage.oneHundred());
	}

	/**
	 * Add a single beneficiary with the specified allocation percentage.
	 * 
	 * @param beneficiaryName
	 *            the name of the beneficiary (should be unique)
	 * @param allocationPercentage
	 *            the beneficiary's allocation percentage within this account
	 */
	public void addBeneficiary(String beneficiaryName,
			Percentage allocationPercentage) {
		beneficiaries
				.add(new Beneficiary(beneficiaryName, allocationPercentage));
	}

	/**
	 * Returns the beneficiaries for this account.
	 * <p>
	 * Callers should not attempt to hold on or modify the returned set. This
	 * method should only be used transitively; for example, called to
	 * facilitate account reporting.
	 * 
	 * @return the beneficiaries of this account
	 */
	public Set<Beneficiary> getBeneficiaries() {
		return Collections.unmodifiableSet(beneficiaries);
	}

	/**
	 * Returns a single account beneficiary. Callers should not attempt to hold
	 * on or modify the returned object. This method should only be used
	 * transitively; for example, called to facilitate reporting or testing.
	 * 
	 * @param name
	 *            the name of the beneficiary e.g "Annabelle"
	 * @return the beneficiary object
	 */
	public Beneficiary getBeneficiary(String name) {
		for (Beneficiary b : beneficiaries) {
			if (b.getName().equals(name)) {
				return b;
			}
		}
		throw new IllegalArgumentException("No such beneficiary with name '"
				+ name + "'");
	}

	public void setBeneficiaries(Set<Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Account other = (Account) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;

		return true;
	}

	public String toString() {
		return "[Account (" + entityId + ") number = '" + number + "', name = "
				+ name + "'] + beneficiaries = " + beneficiaries;
	}

}