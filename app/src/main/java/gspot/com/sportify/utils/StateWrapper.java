package gspot.com.sportify.utils;

/**
 * Created by patrickhayes on 5/11/16.
 * This is a wrapper class for the state enum.
 * The enum represents the four states of a profile
 * 1. Edit (All fields are enabled)
 * 2. View My Own (I can see everything and go to edit mode)
 * 3. View My Teammates (I can see their contact info)
 * 4. View Other (I can only see their name, bio, and sports bios
 */
public class StateWrapper {

    /**
     * State Enum Class
     * ProfileActivity exists in 3 types of states:
     * - View Mine State: The user is viewing their own profile
     * - Edit State: The user is editing their own profile
     * - View Other State: The user is viewing someone else's profile
     * Depending on the state that ProfileActivity is in, the user
     * may or may not be able to edit particular parts of the page.
     */
    public enum State {
        EDIT("Edit"),
        VIEW_MINE("View Mine"),
        VIEW_TEAMMATE("View Other"),
        VIEW_OTHER("View Other");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private State mState;

    public StateWrapper(State state) {
        mState = state;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        this.mState = state;
    }
}
