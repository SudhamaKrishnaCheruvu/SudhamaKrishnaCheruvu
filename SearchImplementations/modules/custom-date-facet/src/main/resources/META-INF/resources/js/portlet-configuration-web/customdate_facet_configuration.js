
AUI.add(
	'liferay-search-custom-date-facet-configuration',
	(A) => {
		var CustomDateFacetConfiguration = function (form) {
			var instance = this;

			instance.form = form;

			instance.form.on('submit', A.bind(instance._onSubmit, instance));
		};

		A.mix(CustomDateFacetConfiguration.prototype, {
			_onSubmit(event) {
				var instance = this;

				event.preventDefault();

				var ranges = [];

				var rangeFormRows = A.all('.range-form-row').filter((item) => {
					return !item.get('hidden');
				});

				rangeFormRows.each((item) => {
					var label = item.one('.label-input').val();

					var range = item.one('.range-input').val();

					ranges.push({
						label,
						range,
					});
				});

				instance.form.one('.ranges-input').val(JSON.stringify(ranges));

				submitForm(instance.form);
			},
		});

		Liferay.namespace(
			'Search'
		).CustomDateFacetConfiguration = CustomDateFacetConfiguration;
	},
	'',
	{
		requires: ['aui-node'],
	}
);