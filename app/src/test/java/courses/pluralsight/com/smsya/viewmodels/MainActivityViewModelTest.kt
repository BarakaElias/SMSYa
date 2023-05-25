package courses.pluralsight.com.smsya.viewmodels

import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.services.ContactsServices
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.*
import retrofit2.Response

internal class MainActivityViewModelTest {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var contactsRepositoryMock: ContactsRepository
    private lateinit var contactsServices: ContactsServices

    @Before
    fun setup(){
        contactsRepositoryMock = mock(ContactsRepository::class.java)
        contactsServices = mock(ContactsServices::class.java)
        viewModel = MainActivityViewModel(contactsRepositoryMock)
    }
    @Test
    fun getAddressBooks_success() = runBlockingTest{
        //Given a fresh viewmodel
        val addressBooks = listOf(
            AddressBook("aaa","Address book 1", description = "Test book 1"),
            AddressBook("bbb","Addressbook 2", description = "Test book 2")
        )

//        `when`(viewModel.getAddressBooks()).thenReturn()

        `when`(contactsRepositoryMock.getAddressBooks()).thenReturn(addressBooks)

        //When
        viewModel.getAddressBooks()


        //Then
        verify(contactsRepositoryMock).getAddressBooks()
        assert(viewModel.isLoading.value == false)
        assert(viewModel.hasError.value == false)
        assert(viewModel.errorMessage.value == null)
    }




}