package dev.syndek.chronopay

internal class PlayerData {
    var onlineTime = 0
        private set
    var payedMoney = 0.0
        private set

    fun incrementOnlineTime() {
        onlineTime++
    }

    fun resetOnlineTime() {
        onlineTime = 0
    }

    fun addPayedMoney(amount: Double) {
        payedMoney += amount
    }

    fun resetPayedMoney() {
        payedMoney = 0.0
    }
}