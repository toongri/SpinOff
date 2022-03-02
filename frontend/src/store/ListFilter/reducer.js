import { createReducer } from '@reduxjs/toolkit';
import { Toggle_Filter, Discovery, Following } from './action';

const initialState = 'discovery';

const listFilterReducer = createReducer(initialState, {
  [Toggle_Filter]: state => {
    return state === 'discovery' ? 'following' : 'discovery';
  },
  [Discovery]: () => {
    return 'discovery';
  },
  [Following]: () => {
    return 'following';
  },
});

export default listFilterReducer;
