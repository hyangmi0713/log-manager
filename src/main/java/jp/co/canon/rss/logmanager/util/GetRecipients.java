package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.repository.AddressBookRepository;
import jp.co.canon.rss.logmanager.repository.GroupBookRepository;
import jp.co.canon.rss.logmanager.vo.job.StepEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetRecipients {
    AddressBookRepository addressRepository;
    GroupBookRepository groupRepository;

    public GetRecipients(AddressBookRepository addressRepository, GroupBookRepository groupRepository) {
        this.addressRepository = addressRepository;
        this.groupRepository = groupRepository;
    }

    public String [] getRecipients(StepEntity stepEntity) {
        List<String> recipients = new ArrayList<>();

        // custom mail list
        recipients.addAll(Arrays.asList(stepEntity.getCustomEmails()));

        // address book mail list
        if(stepEntity.getEmailBookIds()!=null) {
            for(long addressId : stepEntity.getEmailBookIds())
                recipients.add(addressRepository.findById(addressId).get().getEmail());
        }

        // group book mail list
        if(stepEntity.getGroupBookIds()!=null) {
            for(long groupId : stepEntity.getGroupBookIds()) {
                List<Long> groupAddressId = groupRepository.getAddressIdList(groupRepository.getGroupName(groupId));
                for(long addressId : groupAddressId)
                    recipients.add(addressRepository.findById(addressId).get().getEmail());
            }
        }

        return recipients.toArray(new String[recipients.size()]);
    }
}
