'use strict'
import React from 'react'

import PricePointInput from '../components/PricePointInput'

const ProductRow = props => (
    <tr>
      <td key="name">{props.product.name}</td>
      <td key="desc">{props.product.description}</td>
      <td key="tags">{props.product.tags.join(', ')}</td>
      <td key="prices">
        <ul>
          {props.product.prices.map(price => <li key={`${props.product.id}_${price.currency}`}>{price.amount} {price.currency}</li>)}
        </ul>
        {props.showSetPrice &&
        <PricePointInput setPrice={(price) => props.setPrice(props.product.id, price)}
                         showSaveButton={true}
                         errors={props.errors}/>
        }
      </td>
      <td key="action">
        <button onClick={() => props.editProduct(props.product.id)} className="btn btn-default btn-sm">Edit</button>
        <button onClick={() => props.addPrice(props.product.id)} className="btn btn-default btn-sm">Add Price</button>
      </td>
    </tr>
)

ProductRow.propTypes = {
  product: React.PropTypes.object.isRequired,
  showSetPrice: React.PropTypes.bool.isRequired,
  editProduct: React.PropTypes.func.isRequired,
  addPrice: React.PropTypes.func.isRequired,
  errors: React.PropTypes.arrayOf(React.PropTypes.string).isRequired
}

export default ProductRow