import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function Profile({ onClick }) {
  return <Icon onClick={onClick} />;
}

Profile.propTypes = {
  onClick: propTypes.func,
};

export default Profile;
