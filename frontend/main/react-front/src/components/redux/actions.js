export const SEARCH = 'SEARCH';
export const UPDATE = 'UPDATE';

export function doSearch(result){
    return {
        type: SEARCH,
        result: result
    }
}