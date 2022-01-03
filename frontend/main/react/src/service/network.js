class Network{
  constructor(key){
    this.key = key;

    this.getRequestOptions = {
      method: 'GET',
      redirect: 'follow'
    };
  }

  search(query){
    return fetch(`https://www.googleapis.com/youtube/v3/search?key=${this.key}&part=snippet&maxResults=50&q=${query}&type=video`, this.getRequestOptions)
    .then(response => response.json())
    .then(result => result.items.map(item =>({...item, id: item.id.video})));
  }
}

export default Network;