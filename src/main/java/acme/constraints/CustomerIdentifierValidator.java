
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.features.authenticated.customer.CustomerRepository;
import acme.realms.Customer;

@Validator
public class CustomerIdentifierValidator extends AbstractValidator<ValidCustomerIdentifier, String> {

	@Autowired
	private CustomerRepository repository;


	@Override
	public void initialize(final ValidCustomerIdentifier annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String identifier, final ConstraintValidatorContext context) {

		boolean result;

		boolean validNotNull = true;
		boolean validNotDuplication = true;
		boolean validIdentifier = true;

		if (identifier == null)
			validNotNull = false;

		if (!identifier.matches("^[A-Z]{2,3}\\d{6}$"))
			validIdentifier = false;

		Customer customer = this.repository.findByIdentifier(identifier).getFirst();
		DefaultUserIdentity identity = customer.getIdentity();

		String nameInitial = String.valueOf(identity.getName().charAt(0)).toUpperCase();
		String surnameInitial = String.valueOf(identity.getSurname().charAt(0)).toUpperCase();

		String initials = nameInitial + surnameInitial;

		validIdentifier = StringHelper.startsWith(identifier, initials, true);

		List<Customer> duplicateIdentifierCustomers = this.repository.findByIdentifier(identifier);
		if (duplicateIdentifierCustomers.size() > 1)
			validNotDuplication = false;

		super.state(context, validNotNull, "identifierNumber", "acme.validation.manager.invalid-identifier-notNull.message");
		super.state(context, validIdentifier, "identifierNumber", "acme.validation.manager.invalid-identifier.message");
		super.state(context, validNotDuplication, "identifierNumber", "acme.validation.manager.invalid-identifier-not-duplication.message");

		result = !super.hasErrors(context);

		return result;
	}

}
