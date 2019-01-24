import React from "react";
import {Field} from "redux-form";
import {
  Row,
  Col,
  FormGroup,
  ControlLabel,
  FieldLevelHelp
} from "patternfly-react";
import FormattedMessage from "ui/i18n/FormattedMessage";
import SwitchRenderer from "ui/common/form/SwitchRenderer";

const renderField = ({input, label, min, max, direction, className}) => {
  return (
    <FormGroup className="GeneralSettings__field-form-group">
      <ControlLabel htmlFor={input.name} className={className}>
        <FormattedMessage id={label} />
      </ControlLabel>

      <input
        {...input}
        type="number"
        className="GeneralSettings__input-number"
        min={min}
        max={max}
        dir={direction}
      />
      <div className="GeneralSettings__input-number-addon">PX</div>
    </FormGroup>
  );
};

const GeneralSettings = () => (
  <div className="GeneralSettings">
    <Row>
      <Col xs={12}>
        <label className="GeneralSettings__title">
          <FormattedMessage id="plugin.chart.generalSettings" />
        </label>
      </Col>
    </Row>
    <Row>
      <Col xs={4} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <ControlLabel>
            <strong>
              <FormattedMessage id="plugin.chart.chartSize" />
            </strong>
          </ControlLabel>
        </FormGroup>
      </Col>
      <Col xs={4} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <ControlLabel>
            <strong>
              <FormattedMessage id="plugin.chart.padding" />
            </strong>
          </ControlLabel>
        </FormGroup>
      </Col>
    </Row>
    <Row>
      <Col xs={4} className="GeneralSettings__col">
        <Field
          type="number"
          component={renderField}
          name="size.height"
          label="plugin.chart.height"
          min="0"
          max="9999"
          direction="rtl"
          className="GeneralSettings__field-height"
        />
      </Col>
      <Col xs={6} className="GeneralSettings__col">
        <Row>
          <Col xs={6}>
            <Field
              type="number"
              component={renderField}
              name="padding.top"
              label="plugin.chart.top"
              min="0"
              max="9999"
              direction="rtl"
              className="GeneralSettings__field-top"
            />
          </Col>
          <Col xs={6}>
            <Field
              type="number"
              component={renderField}
              name="padding.right"
              label="plugin.chart.right"
              min="0"
              max="9999"
              direction="rtl"
              className="GeneralSettings__field-right"
            />
          </Col>
        </Row>
      </Col>
    </Row>
    <Row>
      <Col xs={4} className="GeneralSettings__col">
        <Field
          type="number"
          component={renderField}
          name="size.with"
          label="plugin.chart.width"
          min="0"
          max="9999"
          direction="rtl"
          className="GeneralSettings__field-width"
        />
      </Col>
      <Col xs={6} className="GeneralSettings__col">
        <Row>
          <Col xs={6}>
            <Field
              type="number"
              component={renderField}
              name="padding.bottom"
              label="plugin.chart.bottom"
              min="0"
              max="9999"
              direction="rtl"
              className="GeneralSettings__field-bottom"
            />
          </Col>
          <Col xs={6}>
            <Field
              type="number"
              component={renderField}
              name="padding.left"
              label="plugin.chart.left"
              min="0"
              max="9999"
              direction="rtl"
              className="GeneralSettings__field-left"
            />
          </Col>
        </Row>
      </Col>
    </Row>
    <Row>
      <Col xs={12} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <ControlLabel>
            <strong>
              <FormattedMessage id="plugin.chart.interactiveChart" />
            </strong>
            <FieldLevelHelp
              content={<FormattedMessage id="plugin.chart.Y2axis.help" />}
              close="true"
            />
          </ControlLabel>
        </FormGroup>
      </Col>
    </Row>
    <Row>
      <Col xs={4} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <ControlLabel>
            <FormattedMessage id="plugin.chart.showDetailMouseClick" />
          </ControlLabel>
          &nbsp;&nbsp;
          <Field component={SwitchRenderer} name="iteraction.enabled" />
        </FormGroup>
      </Col>
      <Col xs={2} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <ControlLabel>
            <FormattedMessage id="plugin.chart.legendPosition" />
          </ControlLabel>
        </FormGroup>
      </Col>
      <Col xs={4} className="GeneralSettings__col">
        <FormGroup className="GeneralSettings__form-group">
          <label className="radio-inline">
            <Field
              component="input"
              type="radio"
              name="legend.position"
              value="bottom"
            />
            <FormattedMessage id="plugin.chart.bottom" />
          </label>
          <label className="radio-inline">
            <Field
              component="input"
              type="radio"
              name="legend.position"
              value="right"
            />
            <FormattedMessage id="plugin.chart.right" />
          </label>
        </FormGroup>
      </Col>
    </Row>
  </div>
);

export default GeneralSettings;
