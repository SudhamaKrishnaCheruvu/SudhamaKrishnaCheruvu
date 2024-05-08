AUI.add(
	'liferay-search-create-date-facet',
	(A) => {
		var DEFAULTS_FORM_VALIDATOR = A.config.FormValidator;

		var FacetUtil = Liferay.Search.FacetUtil;
		var Util = Liferay.Util;

		var CreateDateFacetFilter = function (config) {
			var instance = this;

			instance.form = config.form;
			instance.fromInputDatePicker = config.fromInputDatePicker;
			instance.fromInputName = config.fromInputName;
			instance.namespace = config.namespace;
			instance.searchCustomRangeButton = config.searchCustomRangeButton;
			instance.toInputDatePicker = config.toInputDatePicker;
			instance.toInputName = config.toInputName;

			instance.fromInput = A.one('#' + instance.fromInputName);
			instance.toInput = A.one('#' + instance.toInputName);

			instance._initializeFormValidator();

			if (instance.searchCustomRangeButton) {
				instance.searchCustomRangeButton.on(
					'click',
					A.bind(instance.filter, instance)
				);
			}

			if (instance.fromInput) {
				instance.fromInput.on('keydown', instance._onDateInputKeyDown);
			}

			if (instance.toInput) {
				instance.toInput.on('keydown', instance._onDateInputKeyDown);
			}
		};

		var CreateDateFacetFilterUtil = {
			clearSelections() {
				var param = this.getParameterName();
				var paramFrom = param + 'From';
				var paramTo = param + 'To';

				var parameterArray = document.location.search
					.substr(1)
					.split('&');

				parameterArray = FacetUtil.removeURLParameters(
					param,
					parameterArray
				);

				parameterArray = FacetUtil.removeURLParameters(
					paramFrom,
					parameterArray
				);

				parameterArray = FacetUtil.removeURLParameters(
					paramTo,
					parameterArray
				);

				this.submitSearch(parameterArray.join('&'));
			},

			getParameterName() {
				return fieldName;
			},

			submitSearch(parameterString) {
				document.location.search = parameterString;
			},

			/**
			 * Formats a date to 'YYYY-MM-DD' format.
			 * @param {Date} date The date to format.
			 * @returns {String} The date string.
			 */
			toLocaleDateStringFormatted(date) {
				var localDate = new Date(date);

				localDate.setMinutes(
					date.getMinutes() - date.getTimezoneOffset()
				);

				return localDate.toISOString().split('T')[0];
			},
		};

		A.mix(CreateDateFacetFilter.prototype, {
			_initializeFormValidator() {
				var instance = this;

				var dateRangeRuleName = instance.namespace + 'dateRange';

				A.mix(
					DEFAULTS_FORM_VALIDATOR.STRINGS,
					{
						[dateRangeRuleName]: Liferay.Language.get(
							'search-custom-range-invalid-date-range'
						),
					},
					true
				);

				A.mix(
					DEFAULTS_FORM_VALIDATOR.RULES,
					{
						[dateRangeRuleName]() {
							return A.Date.isGreaterOrEqual(
								instance.toInputDatePicker.getDate(),
								instance.fromInputDatePicker.getDate()
							);
						},
					},
					true
				);

				var customRangeValidator = new A.FormValidator({
					boundingBox: instance.form,
					fieldContainer: 'div',
					on: {
						errorField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								true
							);
						},
						validField() {
							Util.toggleDisabled(
								instance.searchCustomRangeButton,
								false
							);
						},
					},
					rules: {
						[instance.fromInputName]: {
							[dateRangeRuleName]: true,
						},
						[instance.toInputName]: {
							[dateRangeRuleName]: true,
						},
					},
				});

				var onRangeSelectionChange = function () {
					customRangeValidator.validate();
				};

				if (instance.fromInputDatePicker) {
					instance.fromInputDatePicker.on(
						'selectionChange',
						onRangeSelectionChange
					);
				}

				if (instance.toInputDatePicker) {
					instance.toInputDatePicker.on(
						'selectionChange',
						onRangeSelectionChange
					);
				}
			},

			_onDateInputKeyDown(event) {
				if (!event.isKey('TAB')) {
					event.preventDefault();
				}
			},

			filter() {
				var instance = this;

				var fromDate = instance.fromInputDatePicker.getDate();

				var toDate = instance.toInputDatePicker.getDate();

				var modifiedFromParameter = CreateDateFacetFilterUtil.toLocaleDateStringFormatted(
					fromDate
				);

				var modifiedToParameter = CreateDateFacetFilterUtil.toLocaleDateStringFormatted(
					toDate
				);

				var param = CreateDateFacetFilterUtil.getParameterName();
				var paramFrom = param + 'From';
				var paramTo = param + 'To';

				var parameterArray = document.location.search
					.substr(1)
					.split('&');

				parameterArray = FacetUtil.removeURLParameters(
					param,
					parameterArray
				);

				parameterArray = FacetUtil.removeURLParameters(
					paramFrom,
					parameterArray
				);

				parameterArray = FacetUtil.removeURLParameters(
					paramTo,
					parameterArray
				);

				var startParameterNameElement = document.getElementById(
					instance.namespace + 'start-parameter-name'
				);

				if (startParameterNameElement) {
					parameterArray = FacetUtil.removeURLParameters(
						startParameterNameElement.value,
						parameterArray
					);
				}

				parameterArray = FacetUtil.addURLParameter(
					paramFrom,
					modifiedFromParameter,
					parameterArray
				);

				parameterArray = FacetUtil.addURLParameter(
					paramTo,
					modifiedToParameter,
					parameterArray
				);

				CreateDateFacetFilterUtil.submitSearch(parameterArray.join('&'));
			},
		});

		Liferay.namespace('Search').CreateDateFacetFilter = CreateDateFacetFilter;

		Liferay.namespace(
			'Search'
		).CreateDateFacetFilterUtil = CreateDateFacetFilterUtil;
	},
	'',
	{
		requires: ['aui-form-validator', 'liferay-search-facet-util'],
	}
);