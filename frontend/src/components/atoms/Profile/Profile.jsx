import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function Profile({ padding = '' }) {
  return <Icon padding={padding} />;
}

Profile.propTypes = {
  padding: propTypes.string,
};

export default Profile;
