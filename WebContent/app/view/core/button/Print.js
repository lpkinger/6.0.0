/**
 * 打印按钮
 */	
Ext.define('erp.view.core.button.Print',{ 
		id:'print',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
        printType:'',
        fireHandler: function(e){
        	var me = this,
            handler = me.handler;
            Ext.Ajax.request({
	    	  url : basePath + 'common/JasperReportPrint/getPrintType.action',
	    		   method : 'get',
	    		   async : false,
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
					   if(r.success && r.printtype){
					   		me.printType=r.printtype;
	    			   }
	    		   }
	    	   });
            if(me.printType!='jasper'||caller=='customzl'){
            	 me.fireEvent('click', me, e);
            }
	        if (handler) {
	            handler.call(me.scope || me, me, e);
	        }
        	me.onBlur();
	    },
	    handler:function(btn){
	    	var me=this;
            if(me.printType=='jasper'&&caller!='customzl'){
            	me.print(btn);
            }
	    },
		initComponent : function(){
			var me=this;
			this.callParent(arguments); 
		},
		print : function(btn) {
			var form = btn.ownerCt.ownerCt;
			var keyField = form.keyField;
			var id = Ext.getCmp(keyField).value;
			var isProdIO = false;
			if (caller == 'BOM!BOMCostDetail!Print' && (id == "" || id == null)) {
				showError("请先选择需要打印成本的BOM");
				return;
			}
			if (Ext.getCmp('pi_class')) {
				isProdIO = true;// 出入库单据
			}
			form.setLoading(true);
			// 调用后台存储过程，报表默认为原报表名
			Ext.Ajax.request({
				url : basePath
						+ 'common/JasperReportPrint/JasperGetReportnameByProcedure.action',
				params : {
					ids : id,
					caller:caller,
					reportname:''
				},
				method : 'post',
				timeout : 360000,
				callback : function(options, success, response) {
					var res = new Ext.decode(response.responseText);
					if (res.success) {
						var str = res.reportname.split("#");
						console.log("str:"+str);
						if (str != null) {
							for (i = 0; i <= str.length; i++) {
								if (str[i]!=""&&str[i] != null && str[i].length > 0) {
									 var reportnames=str[i];
									Ext.Ajax.request({
										url : basePath
												+ 'common/JasperReportPrint/printDefault.action',
										params : {
											id : id,
											caller : caller,
											reportname : reportnames,
											isProdIO : isProdIO
										},
										method : 'post',
										timeout : 360000,
										callback : function(options, success,
												response) {
											form.setLoading(false);
											var res = new Ext.decode(response.responseText);
											if (res.success) {
												var url = res.info.printurl
														+ '?userName='
														+ res.info.userName
														+ '&reportName='
														+ reportnames
														+ '&whereCondition='
														+ encodeURIComponent(res.info.whereCondition)
														+ '&printType='
														+ res.info.printtype
														+ '&title='
														+ res.info.title;
												window.open(url, '_blank');
											} else if (res.exceptionInfo) {
												var str = res.exceptionInfo;
												showError(str);
												return;
											}
										}
									});
								}
							}
						}
	
					} else {
						Ext.Ajax.request({
							url : basePath
									+ 'common/JasperReportPrint/printDefault.action',
							params : {
								id : id,
								caller : caller,
								reportname : '',
								isProdIO : isProdIO
							},
							method : 'post',
							timeout : 360000,
							callback : function(options, success, response) {
								form.setLoading(false);
								var res = new Ext.decode(response.responseText);
								if (res.success) {
									var url = res.info.printurl
											+ '?userName='
											+ res.info.userName
											+ '&reportName='
											+ res.info.reportName
											+ '&whereCondition='
											+ encodeURIComponent(res.info.whereCondition)
											+ '&printType=' + res.info.printtype
											+ '&title=' + res.info.title;
									window.open(url, '_blank');
								} else if (res.exceptionInfo) {
									var str = res.exceptionInfo;
									showError(str);
									return;
								}
							}
						});
					}
				}
			});
		}
	});