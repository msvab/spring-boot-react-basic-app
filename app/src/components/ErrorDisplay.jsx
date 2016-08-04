'use strict'
import React from 'react'

export default props => {
  if (props.errors.length > 0)
    return <p className="bg-danger">{props.errors.map((error, index) => {
      if (index === props.errors.length - 1) {
        return <span key={index}>{error}</span>
      } else {
        return <span key={index}>{error}<br/></span>
      }
    })}</p>
  return null
}