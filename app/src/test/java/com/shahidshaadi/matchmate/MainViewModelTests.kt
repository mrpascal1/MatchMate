package com.shahidshaadi.matchmate

import app.cash.turbine.test
import com.shahidshaadi.matchmate.data.remote.Dob
import com.shahidshaadi.matchmate.data.remote.Location
import com.shahidshaadi.matchmate.data.remote.Login
import com.shahidshaadi.matchmate.data.remote.Name
import com.shahidshaadi.matchmate.data.remote.Picture
import com.shahidshaadi.matchmate.data.remote.User
import com.shahidshaadi.matchmate.data.remote.UserResponse
import com.shahidshaadi.matchmate.data.repository.MatchRepository
import com.shahidshaadi.matchmate.model.MatchProfile
import com.shahidshaadi.matchmate.model.toMatchProfile
import com.shahidshaadi.matchmate.ui.state.MainUiState
import com.shahidshaadi.matchmate.utils.NetworkMonitor
import com.shahidshaadi.matchmate.viewmodel.MainViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTests {

    @Mock
    private lateinit var mockRepository: MatchRepository

    @Mock
    private lateinit var mockNetworkMonitor: NetworkMonitor

    private lateinit var viewModel: MainViewModel

    private val mainDispatcher = StandardTestDispatcher()

    private val dummyUsers: ArrayList<MatchProfile> = arrayListOf()

    private fun dummyUser() {
        dummyUsers.clear()
        repeat(2) {
            dummyUsers.add(
                User(
                    name = Name(
                        first = "John $it",
                        last = "Doe"
                    ),
                    dob = Dob(
                        age = 30
                    ),
                    picture = Picture(
                        large = "https://randomuser.me/api/portraits/men/1.jpg"
                    ),
                    gender = "male",
                    email = "johndoe@example.com",
                    phone = "+1-202-555-0191",
                    login = Login(
                        uuid = "123e4567-e89b-12d3-a456-426614174000"
                    ),
                    location = Location(
                        city = "New York",
                        state = "New York",
                        country = "United States"
                    )
                ).toMatchProfile(page = 1)
            )
        }
    }

    private val matchAllMatchesFlow = MutableStateFlow<List<MatchProfile>>(emptyList())
    private val matchNetworkFlow = MutableStateFlow(false)



    @Before
    fun setup() {
        dummyUser()
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(mainDispatcher)
        whenever(mockRepository.allMatches).thenReturn(matchAllMatchesFlow)
        whenever(mockNetworkMonitor.isConnected).thenReturn(matchNetworkFlow)

        viewModel = MainViewModel(mockRepository, mockNetworkMonitor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given cached matches, uiState should show loading then success with matches`() = runTest {

        viewModel.uiState.test {
            val first = awaitItem()
            assertTrue(first is MainUiState.Loading)

            matchAllMatchesFlow.value = dummyUsers

            val second = awaitItem()
            assertTrue(second is MainUiState.Success)
            assertEquals(dummyUsers, (second as MainUiState.Success).matches)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `When network connects and no data fetched, should trigger initial load`() = runTest {
        matchAllMatchesFlow.value = emptyList()
        matchNetworkFlow.value = false

        viewModel.isLoadingMore.test {
            assertFalse(awaitItem())
            matchNetworkFlow.value = true

            assertTrue(awaitItem())
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(mockRepository).initializePagination()
        verify(mockRepository).loadInitialPage()
    }

    @Test
    fun `loadMore should call next page when online and not loading`() = runTest {
        matchNetworkFlow.value = true
        viewModel.loadMore()

        advanceUntilIdle()

        verify(mockRepository).loadNextPage()
    }

    @Test
    fun `onAccept should call repository with accepted status`() = runTest {
        val matchId = "1"
        viewModel.onAccept(matchId)

        advanceUntilIdle()
        verify(mockRepository).updateMatchStatus(matchId, "accepted")
    }

    @Test
    fun `onDecline should call repository with decline status`() = runTest {
        val matchId = "1"
        viewModel.onDecline(matchId)
        advanceUntilIdle()
        verify(mockRepository).updateMatchStatus(matchId, "declined")
    }
}