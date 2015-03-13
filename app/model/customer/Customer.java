package model.customer;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigInteger;

/**
 * Created by mbp-sm on 3/13/15.
 */
@ApiModel("Customer")
public class Customer {
    @ApiModelProperty(
            required = false,
            notes = "Unique Id for a customer. Do not populate when lcreating a new customer",
            dataType = "Long"
    )
    public Long uid;
    @ApiModelProperty(
            required = true,
            notes = "Company Name",
            dataType = "String"
    )
    public String companyName;
    @ApiModelProperty(
            required = true,
            notes = "Company phone number",
            dataType = "String"
    )
    public String phoneNumber;
    @ApiModelProperty(
            required = true,
            notes = "Name of customer contact person",
            dataType = "String"
    )
    public String contactName;
    @ApiModelProperty(
            required = false,
            notes = "Customer Reference No (if provided)",
            dataType = "String"
    )
    public String customerRefNo;

    public Customer() {}

    public Customer (
            final Long _uid,
            final String _companyName, final String _phoneNumber,
            final String _contactName, final String _customerRefNo) {
        this.uid = _uid;
        this.companyName = _companyName;
        this.phoneNumber = _phoneNumber;
        this.contactName = _contactName;
        this.customerRefNo = _customerRefNo;
    }

}
