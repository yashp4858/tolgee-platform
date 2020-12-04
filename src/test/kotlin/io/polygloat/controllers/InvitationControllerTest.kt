package io.polygloat.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.polygloat.Assertions.Assertions.assertThat
import io.polygloat.dtos.response.InvitationDTO
import io.polygloat.model.Permission
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class InvitationControllerTest : SignedInControllerTest() {

    inline fun <reified T> MvcResult.parseResponseTo(): T {
        return jacksonObjectMapper().readValue(this.response.contentAsString)
    }

    @BeforeMethod
    fun login() {
        logAsUser(initialUsername, initialPassword)
    }

    @Test
    fun getRepositoryInvitations() {
        val repository = dbPopulator.createBase(generateUniqueString())
        val invitation = invitationService.create(repository, Permission.RepositoryPermissionType.MANAGE)
        val response = performGet("/api/invitation/list/${repository.id}").andExpect(status().isOk).andReturn()
        val list: List<InvitationDTO> = response.parseResponseTo()
        assertThat(list).hasSize(1);
        assertThat(list[0].code).isEqualTo(invitation)
    }

    @Test
    fun acceptInvitation() {
        val repository = dbPopulator.createBase(generateUniqueString())
        val invitation = invitationService.create(repository, Permission.RepositoryPermissionType.EDIT);

        val newUser = dbPopulator.createUser(generateUniqueString(), "pwd")
        logAsUser(newUser.username, "pwd");
        performGet("/api/invitation/accept/${invitation}").andExpect(status().isOk).andReturn()

        assertThat(invitationService.getForRepository(repository)).hasSize(0);
        assertThat(permissionService.getRepositoryPermission(repository.id, newUser)).isNotEmpty
        val type = permissionService.getRepositoryPermission(repository.id, newUser).get().type
        assertThat(type).isEqualTo(Permission.RepositoryPermissionType.EDIT);
    }
}