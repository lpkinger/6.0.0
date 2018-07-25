Ext.define('erp.view.core.trigger.LongitudeAndLatitude',
				{	extend : 'Ext.form.field.Trigger',
					alias : 'widget.longitudeandlatitudetrigger',
					requiers : [ 'erp.view.core.form.TimeMinuteField',
							'erp.view.core.picker.TimePicker','erp.view.core.picker.HighlightableDatePicker' ],
					triggerCls : 'x-form-freq-trigger',
					onTriggerClick : function() {
						var me = this;
							if(me.value!=null)
							var url='https://api.map.baidu.com/geocoder/v2/?address='+me.value+'&output=json&ak=YOgN88tPirPFHBOk32EjRNQIeGh1z1n6&callback=showLocation';
							Ext.data.JsonP.request({
								url:url,
				                method: 'POST',
				                success: function (response) {
				                	console.log(response);
				                	if(response.status=="0"){
										Ext.getCmp(me.secondname.split('#')[1]).setValue(response.result.location.lng);
										Ext.getCmp(me.secondname.split('#')[0]).setValue(response.result.location.lat);	
				                	}else if(response.status=="1"){
				                		showError("该地址无法获取经纬度");
				                	}
				                },
				                failure: function (response, options) {
				                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
				                }
				            });	
							
						}
				});


							