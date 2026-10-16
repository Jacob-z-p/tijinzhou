import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { LoginUser } from "@/types/api";

type UserState = {
  loginUser: LoginUser | null;
  loaded: boolean;
};

const initialState: UserState = {
  loginUser: null,
  loaded: false,
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setLoginUser(state, action: PayloadAction<LoginUser | null>) {
      state.loginUser = action.payload;
      state.loaded = true;
    },
  },
});

export const { setLoginUser } = userSlice.actions;
export default userSlice.reducer;
