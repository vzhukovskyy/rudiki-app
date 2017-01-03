package ua.pp.rudiki;

public interface SwitchStateListener {
    void onSwitchStateRequestIssued();
    void onSwitchStateReceived(String response);
}
