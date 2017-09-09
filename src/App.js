import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import {FormControl,FormGroup,HelpBlock,ControlLabel} from 'react-bootstrap';
import '../node_modules/react-datetime/css/react-datetime.css';
import DateTime from 'react-datetime';
import moment from 'moment';
require('moment/locale/tr');
class App extends Component {
  constructor(props){
    super(props);
    this.state={value:'',
    date: "15/09/2017 12:06",
    format: "YYYY-MM-DD",
    inputFormat: "DD/MM/YYYY HH:mm",
    mode: "date"};
    this.handleChange=this.handleChange.bind(this);
    this.handleChange1=this.handleChange1.bind(this);
    
  }
  getValidationState() {
    const length = this.state.value.length;
    if (length > 10) return 'success';
    else if (length > 5) return 'warning';
    else if (length > 0) return 'error';
  }

  handleChange(e) {
    this.setState({ value: e.target.value });
  }
  handleChange1(e) {
    if (e.isValid) {
      console.log("newDate", e.format('DD/MM/YYYY HH:mm'));
      this.setState({date: e.format('DD/MM/YYYY HH:mm')});
    }
    
  }
  render() {
    const {date, format, mode, inputFormat} = this.state;
    return (
      <div>
        <form>
        <FormGroup
          controlId="formBasicText"
          validationState={this.getValidationState()}
        >
          <ControlLabel>Working example with validation</ControlLabel>
          <FormControl
            type="text"
            value={this.state.value}
            placeholder="Enter text"
            onChange={this.handleChange}
          />
          <FormControl.Feedback />
          <HelpBlock>Validation is based on string length.</HelpBlock>
        </FormGroup>
        <DateTime dateFormat="DD/MM/YYYY" defaultValue={moment()}
        onChange={this.handleChange1} />
         <FormGroup
          controlId="formBasicText222"
          validationState={this.getValidationState()}
        >
          <ControlLabel>Working example with validation</ControlLabel>
          <FormControl
            type="text"
            value={this.state.value}
            placeholder="Enter text"
            onBlur={this.handleChange}
          />
          <FormControl.Feedback />
          <HelpBlock>Validation is based on string length.</HelpBlock>
        </FormGroup>

      </form>


      
      </div>
    );
  }
}

export default App;
