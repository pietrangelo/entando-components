import React from "react";
import PropTypes from "prop-types";
import FormattedMessage from "ui/i18n/FormattedMessage";
import {
  Col,
  Card,
  CardTitle,
  CardBody,
  DropdownKebab,
  MenuItem
} from "patternfly-react";

const ServerConfigCard = ({
  configItem,
  onClickRemove,
  onClickTest,
  onClickEdit,
  testConnectionOutcome
}) => {
  let testConnectionDiv = null;
  if (testConnectionOutcome) {
    testConnectionDiv = (
      <div className="card-pf-items">
        <div className="card-pf-item">
          <strong>
            <FormattedMessage id="plugin.config.testConnection" />
          </strong>
        </div>
        <div className="card-pf-item">
          {testConnectionOutcome.toUpperCase() === "SUCCESS" ? (
            <span className="fa fa-check" />
          ) : (
            <span className="fa fa-times" />
          )}
        </div>
      </div>
    );
  }
  return (
    <Col xs={6} sm={4} md={4}>
      <Card className="card-pf card-pf-view">
        <CardTitle>
          <DropdownKebab id="myKebab" className="pull-right" pullRight>
            <MenuItem onClick={onClickTest}>
              <FormattedMessage id="common.test" />
            </MenuItem>
            <MenuItem onClick={onClickEdit}>
              <FormattedMessage id="common.edit" />
            </MenuItem>
            <MenuItem onClick={onClickRemove}>
              <FormattedMessage id="common.remove" />
            </MenuItem>
          </DropdownKebab>
        </CardTitle>
        <CardBody>
          <div className="card-pf-top-element">
            <span className="fa fa-server card-pf-icon-circle" />
          </div>
          <h3 className="card-pf-title text-center">
            <strong>
              <FormattedMessage id="plugin.config.server" />
            </strong>
            <br />
            {configItem.name}
          </h3>
          {testConnectionDiv}
          <div className="card-pf-items">
            <div className="card-pf-item">
              <strong>
                <FormattedMessage id="plugin.config.active" />
              </strong>
            </div>
            <div className="card-pf-item">
              {configItem.active ? (
                <span className="fa fa-check" />
              ) : (
                <span className="fa fa-times" />
              )}
            </div>
          </div>
          <p className="card-pf-info">
            <strong>
              <FormattedMessage id="plugin.config.debug" />
            </strong>
            {configItem.debug ? (
              <FormattedMessage id="plugin.config.enabled" />
            ) : (
              <FormattedMessage id="plugin.config.disabled" />
            )}
          </p>

          <p className="card-pf-info">
            <strong>
              <FormattedMessage id="plugin.config.serverURI" />
            </strong>
            {configItem.uri}
          </p>
        </CardBody>
        <hr />
      </Card>
    </Col>
  );
};

ServerConfigCard.propTypes = {
  configItem: PropTypes.shape({}).isRequired,
  testConnectionOutcome: PropTypes.string,
  onClickRemove: PropTypes.func.isRequired,
  onClickTest: PropTypes.func.isRequired,
  onClickEdit: PropTypes.func.isRequired
};

ServerConfigCard.defaultProps = {
  testConnectionOutcome: ""
};

export default ServerConfigCard;
