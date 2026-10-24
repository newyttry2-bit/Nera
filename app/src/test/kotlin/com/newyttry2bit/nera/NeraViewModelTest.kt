package com.newyttry2bit.nera

import com.newyttry2bit.nera.data.model.AppTab
import com.newyttry2bit.nera.ui.viewmodel.NeraViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class NeraViewModelTest {

    @Test
    fun `default tab is home`() {
        val viewModel = NeraViewModel()

        assertEquals(AppTab.Home, viewModel.uiState.value.selectedTab)
    }
}
