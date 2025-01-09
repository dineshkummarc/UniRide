package com.drdisagree.uniride.ui.screens.student.more.chatbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.ChatMessage
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.data.utils.Constant.CHAT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBoxViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _newMessageState = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val messageSendingState: StateFlow<Resource<Unit>> = _newMessageState.asStateFlow()

    private val _messagesState =
        MutableStateFlow<Resource<List<ChatMessage>>>(Resource.Unspecified())
    val messagesState: StateFlow<Resource<List<ChatMessage>>> = _messagesState.asStateFlow()

    init {
        fetchChatMessages()
    }

    private fun fetchChatMessages() {
        viewModelScope.launch {
            _messagesState.value = Resource.Loading()
        }

        firestore.collection(CHAT_COLLECTION)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _messagesState.value = Resource.Error(error.message.toString())
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val chatMessages = it.toObjects(ChatMessage::class.java)
                    _messagesState.value = Resource.Success(chatMessages)
                }
            }
    }

    fun sendMessage(sender: Student, message: String) {
        viewModelScope.launch {
            _newMessageState.value = Resource.Loading()
        }

        val newMessage = ChatMessage(
            message = message,
            sender = sender
        )

        firestore.collection(CHAT_COLLECTION)
            .add(newMessage)
            .addOnSuccessListener {
                _newMessageState.value = Resource.Success(Unit)
            }
            .addOnFailureListener {
                _newMessageState.value = Resource.Error(it.message.toString())
            }
    }
}