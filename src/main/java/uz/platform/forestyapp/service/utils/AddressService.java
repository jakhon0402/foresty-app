package uz.platform.forestyapp.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Address;
import uz.platform.forestyapp.payload.AddressDto;
import uz.platform.forestyapp.repository.AddressRepo;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    AddressRepo addressRepo;
    public Address saveAddress(AddressDto addressDto){
        return Address.builder()
                .region(addressDto.getRegion())
                .districtOrCity(addressDto.getDistrictOrCity())
                .street(addressDto.getStreet())
                .build();
    }

    public Address editAddress(AddressDto addressDto,Address address){
        address.setRegion(addressDto.getRegion());
        address.setDistrictOrCity(addressDto.getDistrictOrCity());
        address.setStreet(addressDto.getStreet());
        return address;
    }

    public void deleteAddress(Address address){
        addressRepo.delete(address);
    }
}
