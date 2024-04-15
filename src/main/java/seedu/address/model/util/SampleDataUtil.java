package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.AdmissionDate;
import seedu.address.model.person.Dob;
import seedu.address.model.person.Ic;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.Ward;
import seedu.address.model.tag.Tag;


/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    private static final Logger logger = LogsCenter.getLogger(SampleDataUtil.class);

    /**
     * Returns an array of sample persons.
     */
    public static Person[] getSamplePersons() {
        return new Person[]{

            new Person(new Name("Alex Yeoh"), getTagSet("Diabetes"), new Dob("01/01/1990"),
                    new Ic("S1233567A"), new AdmissionDate("01/01/2022"), new Ward("WA"),
                    new Remark("Requires assistance with feeding.")),
            new Person(new Name("Bernice Yu"), getTagSet("FallRisk", "Diabetes"), new Dob("01/01/1990"),
                    new Ic("S1244567A"), new AdmissionDate("02/01/2022"), new Ward("WB"),
                    new Remark("Flip every 2 hours to prevent bed sores.")),
            new Person(new Name("Charlotte Oliveiro"), getTagSet("FallRisk"), new Dob("01/01/1990"),
                    new Ic("S1234577A"), new AdmissionDate("03/01/2022"), new Ward("WC"), new Remark("")),
            new Person(new Name("David Li"), getTagSet("Dementia"), new Dob("01/01/1990"),
                    new Ic("S1234566A"), new AdmissionDate("04/01/2022"), new Ward("WD"), new Remark("")),
            new Person(new Name("Irfan Ibrahim"), getTagSet("NPO"), new Dob("01/01/1990"),
                    new Ic("S2234567A"), new AdmissionDate("05/01/2022"), new Ward("WE"), new Remark("")),
            new Person(new Name("Roy Balakrishnan"), getTagSet("Dementia"), new Dob("01/01/1990"),
                    new Ic("S1234567A"), new AdmissionDate("06/01/2022"), new Ward("WF"), new Remark(""))
        };
    }

    /**
     * Returns an address book containing sample persons.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        try {
            for (Person samplePerson : getSamplePersons()) {
                sampleAb.addPerson(samplePerson);
            }
            return sampleAb;
        } catch (NullPointerException e) {
            logger.warning("Sample data file could not be loaded. "
                    + e.getMessage() + " value detected. "
                    + "Will be starting with an empty AddressBook.");
            return new AddressBook();
        } catch (IllegalArgumentException e) {
            logger.warning("Sample data file could not be loaded. "
                    + e.getMessage() + ". "
                    + "Will be starting with an empty AddressBook.");
            return new AddressBook();
        }
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
