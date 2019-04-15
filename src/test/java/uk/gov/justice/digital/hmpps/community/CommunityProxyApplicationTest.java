package uk.gov.justice.digital.hmpps.community;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunityProxyApplicationTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void testAppLoaded() {
        Assertions.assertThat(context.getDisplayName()).isNotNull();
    }

}
