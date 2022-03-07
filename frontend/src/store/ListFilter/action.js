import { createAction } from '@reduxjs/toolkit';

export const Toggle_Filter = 'ListFilter/Toggle_Filter';
export const Discovery = 'ListFilter/Discovery';
export const Following = 'ListFilter/Following';

export const toggleFilter = createAction(Toggle_Filter);
export const discovery = createAction(Discovery);
export const following = createAction(Following);
