'use strict'
import React from 'react'
import ErrorDisplay from './ErrorDisplay'

export default class PricePointInput extends React.Component {
  static propTypes = {
    showSaveButton: React.PropTypes.bool,
    setPrice: React.PropTypes.func,
    errors: React.PropTypes.arrayOf(React.PropTypes.string)
  }

  static defaultProps = {
    showSaveButton: false
  }

  onSave(event) {
    event.preventDefault()
    const amount = this.refs.amount.value
    const currency = this.refs.currency.value.trim().toUpperCase()
    this.props.setPrice({amount: amount === '' ? null : +amount, currency})
  }

  render() {
    return (
        <div className="form-group-sm form-inline">
          <input type="number" ref="amount" step="0.01" className="form-control" placeholder="Amount"/>
          <input type="text" ref="currency" className="form-control" placeholder="Currency"/>
          <ErrorDisplay errors={this.props.errors}/>
          {this.props.showSaveButton && <button type="button" onClick={::this.onSave}>Save</button>}
        </div>
    )
  }
}