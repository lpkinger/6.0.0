Ext.define('erp.view.core.trigger.ProductType',
				{	extend : 'Ext.form.field.Trigger',
					alias : 'widget.producttypetrigger',
					requiers : [ 'erp.view.core.form.TimeMinuteField','erp.view.core.picker.TimePicker',
						'erp.view.core.picker.HighlightableDatePicker' ],
					triggerCls : 'x-form-autocode-trigger',
					onTriggerClick : function() {
						this.showWin();
					},
					readOnly:true,
					showWin:function(){
						var win = this.win;
						if(!win){
							var win = new Ext.window.Window({
								id : 'win',
								height : '85%',
								width : '45%',
								maximizable : true,
								border:false,
								closeAction : 'hide',
								buttonAlign : 'center',
								layout : 'anchor',
								title : '选择产品类型',
								bodyStyle : 'background:#F2F2F2;',
								items : [{
									tag : 'iframe',
									anchor : '100% 100%',
									layout : 'fit',
									html : '<iframe id="iframe" src="'
											+ basePath
											+ 'jsps/plm/request/ProductType.jsp'
											+ '" height="100%" width="100%" frameborder="0"></iframe>'
								}]
							});	
							this.win = win;
						}
						win.show();
					}
				});


							