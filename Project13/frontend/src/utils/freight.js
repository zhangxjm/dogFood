const BASE_PRICE = 10.00
const PER_KG_PRICE = 3.00
const PER_M3_PRICE = 50.00
const SCALE = 2

function simpleCalculate(weight, volume) {
  if (!weight || weight <= 0) {
    weight = 1.0
  }
  if (!volume || volume <= 0) {
    volume = 0.01
  }

  const weightCharge = weight * PER_KG_PRICE
  const volumeCharge = volume * PER_M3_PRICE

  const maxCharge = Math.max(weightCharge, volumeCharge)
  const totalPrice = BASE_PRICE + maxCharge

  return totalPrice.toFixed(SCALE)
}

function calculate(weight, volume, distance) {
  if (!weight || weight <= 0) {
    weight = 1.0
  }
  if (!volume || volume <= 0) {
    volume = 0.01
  }
  if (!distance || distance <= 0) {
    distance = 1.0
  }

  const weightCharge = weight * PER_KG_PRICE
  const volumeCharge = volume * PER_M3_PRICE

  const chargeByWeight = weightCharge * (distance / 100.0)
  const chargeByVolume = volumeCharge * (distance / 100.0)

  const maxCharge = Math.max(chargeByWeight, chargeByVolume)
  const totalPrice = BASE_PRICE + maxCharge

  return totalPrice.toFixed(SCALE)
}

const FreightCalculator = {
  calculate,
  simpleCalculate
}

export {
  FreightCalculator,
  calculate,
  simpleCalculate
}

export default FreightCalculator
