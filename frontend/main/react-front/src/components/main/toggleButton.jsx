import React from 'react';
import './toggleButton.css';

const toggleButton = () => {
  return (
<label class="switch">
  <input type="checkbox" />
  <span class="slider round"></span>
</label>
  );
};

export default toggleButton;