Ext.define('erp.view.vendbarcode.AcceptNotifyListDetail.AcceptNotifyListDetailGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	layout: 'fit', //fit
	id:'acceptNotifyListDetail',
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.acceptNotifyListDetailGrid',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	hideBorders: true, 
	autoScroll : true,
	selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpVendDatalistToolbar',
        dock: 'bottom',
        items:[{
			cls: 'x-btn-tb',
			iconCls: 'x-button-icon-detail',
			tooltip: $I18N.common.tip.relativelist,
			handler:function(){
				window.location=basePath +'jsps/vendbarcode/acceptNotifylist.jsp?whoami=VendAcceptNotify';
			}
			}],
        displayInfo: true
	}],
	initComponent : function(){ 
		this.getColumnsAndStore();
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	},
	getColumnsAndStore:function(){
		var me = this; 
		Ext.apply(me, { 
				xtype:'gridpanel',
				keyField:'AN_ID',
				title : '送货通知单列表',
				bodyStyle : 'background-color:#f1f1f1;',
				columnLines : true,
				emptyText: '没有数据', 
				 plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				columns : {
					defaults:{
					cls : 'x-grid-header-1'
				},
					items:[{
						xtype:'rownumberer',
						align : 'center',
						width : 35
					},{
						header : 'ID',
						dataIndex : 'AN_ID',
						hidden : true,
						width:0
					},{
						header: "送货通知单号", 
						width : 120,
						dataIndex:'AN_CODE',
						/*flex:1,*/
						filter: {xtype:"textfield", filterName:"AN_CODE"}
					},{
						header: "时间", 
						width : 100,
						dataIndex:'AN_DATE',
						filter: {xtype:"datefield", filterName:"AN_DATE"}
					},{
						header: "状态",
						width : 60,
						dataIndex:'AN_STATUS',
						filter: {xtype:"textfield", filterName:"AN_STATUS"}
					},{
						header: "送货单号", 
						width : 140,
						dataIndex:'AN_SENDCODE',
						filter: {xtype:"textfield", filterName:"AN_SENDCODE"}
					},{
						header: "供应商号", 
						width : 80,
						dataIndex:'AN_VENDCODE',
						filter: {xtype:"textfield", filterName:"AN_VENDCODE"}
					},{
				        header: '供应商',
				        width : 240,
				        dataIndex:'AN_VENDNAME',
						filter: {xtype:"textfield", filterName:"AN_VENDNAME"}
				    },{
				        header: '录入人',
				        width : 80,
				        dataIndex:'AN_RECORDER',
						filter: {xtype:"textfield", filterName:"AN_RECORDER"}
				    },{
				        header: '录入日期',
				        width : 100,
				        dataIndex:'AN_INDATE',
						filter: {xtype:"datefield", filterName:"AN_INDATE"}
				    },{
				        header: '采购单号',
				        width : 130,
				        dataIndex:'AND_ORDERCODE',
						filter: {xtype:"textfield", filterName:"AND_ORDERCODE"}
				    },{
				        header: '采购单序号',
				        width : 80,
				        dataIndex:'AND_ORDERDETNO',
						filter: {xtype:"textfield", filterName:"AND_ORDERDETNO"}
				    },{
				        header: '物料编号',
				        width : 120,
				        dataIndex:'AND_PRODCODE',
						filter: {xtype:"textfield", filterName:"AND_PRODCODE"}
				    },{
				        header: '送货通知单数量',
				        width : 80,
				        dataIndex:'AND_INQTY',
						filter: {xtype:"textfield", filterName:"AND_INQTY"}
				    }]
				},
				store : Ext.create('Ext.data.Store', {
					storeId : 'myStore',
					pageSize : pageSize,
					fields : ['AN_ID', 'AN_CODE', 'AN_DATE', 'AN_STATUS', 'AN_SENDCODE', 'AN_VENDCODE',
						'AN_VENDNAME','AN_RECORDER','AN_INDATE','AND_ORDERCODE','AND_ORDERDETNO','AND_PRODCODE','AND_INQTY'],
					autoLoad : {
						params: {
							caller: caller,
							condition:condition
						}
					},
					proxy : {
						type : 'ajax',
						url : basePath + 'vendbarcode/datalist/getAcceptNotifyListDetail.action',
						reader : {
							type : 'json',
							root : 'datas',
							totalProperty : 'total'
						},
						actionMethods: {
				            read   : 'POST'
				        }
					},
					listeners : {beforeload : function(store) {
						  Ext.apply(store.proxy.extraParams, {caller:caller,condition:condition});
					},
						load:function(store){
							this.dataCount=store.totalCount;
							var toolbar=me.down('erpVendDatalistToolbar');
			        		toolbar.afterOnLoad(1);
						}}
				}),
			/*	dockedItems : [{
					xtype : 'pagingtoolbar',
					dock : 'bottom',
					items:[{
						cls: 'x-btn-tb',
						iconCls: 'x-button-icon-detail',
						tooltip: $I18N.common.tip.relativelist,
						handler:function(){
							window.location=basePath +'jsps/vendbarcode/acceptNotifylist.jsp?whoami=VendAcceptNotify';
						}
						}],
					displayInfo : true,
					store : Ext.data.StoreManager.lookup('myStore'),
					displayMsg:"显示{0}-{1}条数据，共{2}条数据",
					beforePageText: '第',
					afterPageText: '页,共{0}页'
				}]*/
		}); 
	},
	getData:function(grid,caller,condition,page,limit,start){
		var me = this;
		var store = grid.store;
        var data;
        me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'vendbarcode/datalist/getAcceptNotifyListDetail.action',
			params: {
				caller: caller,
				condition: condition,
				page: page,
				limit:pageSize,
				start:0
			},
			callback: function(opt, s, r) {
				me.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);return;
				} else {
					if(rs.datas != null && rs.datas !=''){
						store.loadData(rs.datas);	
						store.dataCount = rs.total;
						if(start == -1){
							    var toolbar=grid.down('erpVendDatalistToolbar');
				                page=1;
				        		toolbar.afterOnLoad(page);
				        		toolbar.child('#first').setDisabled(true);
				        		toolbar.child('#prev').setDisabled(true);
				        		if(rs.total>pageSize){
				        			toolbar.child('#last').setDisabled(false);
					        		toolbar.child('#next').setDisabled(false);
				        		}
						}						
					}
				}
			},
		});

	},
	getCondition: function(isForm){
    	var condition = '';
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if (this.searchGrid) {
    		var s = this.searchGrid.getCondition();
    		if(s != null && s.length > 0) {
    			if(condition.length > 0)
        			condition += ' AND (' + s + ')';
    			else
    				condition = s;
    		}
    	}
    	if(!isForm && this.formCondition) {
    		if(condition.length > 0)
    			condition += ' AND (' + this.formCondition + ')';
			else
				condition = this.formCondition;
    	}
    	if(!Ext.isEmpty(this.filterCondition)||this.fromFilter) {
    		if(!Ext.isEmpty(this.filterCondition)){
	    		if(condition == '') {
	    			condition = this.filterCondition;
	    		} else {
	    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
	    		}
    		}
    	}else if(!Ext.isEmpty(this.defaultFilterCondition)){
    		if(condition == '') {
    			condition = this.defaultFilterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.defaultFilterCondition + ')';
    		}
    	}

    	return condition;// .replace(/=/g, '%3D')
    },
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var me = this ;
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
	                    	if(f.originalxtype == 'numberfield') {
                    			if(value.indexOf('>=')==0||value.indexOf('<=')==0||value.indexOf('>')==0||value.indexOf('<')==0||value.indexOf('!=')==0||value.indexOf('=')==0){
                					if(value.indexOf('!=')==0){
                						value = "("+fn + value + " or "+fn +" is null) ";
                					}else{
                						value = fn + value + " ";
                					}
                    			}else if(value.indexOf('~')>-1){
                    				var arr = value.split('~');
                    				value = fn + " between " + arr[0] + " and "+arr[1]+" ";
                    			}else{
                					value = fn + "=" + value + " ";
                				}
	                    	} else if(f.originalxtype == 'datefield'){
	                    			if(value.indexOf('=')>-1){
	                    				var valueX = value.split('=')[1];
	                    				var length = valueX.split('-').length;
	                    				if(length<3){
	                    					if(length == 1){
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-12-31'));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}else if(length == 2){
	                    						var day = new Date(valueX.split('-')[0],valueX.split('-')[1],0);
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-'+day.getDate()));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}
		                    			}else {
		                    				if(value.indexOf('>=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')>='" + value + "' ";
			                    			}else if(value.indexOf('<=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')<='" + value + "' ";
			                    			}else {
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
			                    			}
		                    			}
	                    			}else if(value.indexOf('~')>-1){
                    					var value1 = Ext.Date.toString(new Date(value.split('~')[0]));
                        				var value2 = Ext.Date.toString(new Date(value.split('~')[1]));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    			}else{
	                    				value = Ext.Date.toString(new Date(value));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
	                    			}
		                        } else {
	                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
	                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
	    	                    	if(exp_d.test(value)){
	    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
	    	                    	} else if(exp_t.test(value)){
	    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
	    	                    	} else{
	    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
	    	                    			if (value == '-所有-') {
	    	                    				value = ' 1=1 ';
	    	                    			} else {
	    	                    				if (f.column && f.column.xtype == 'yncolumn'){
	    	                    					if (value == '-无-') {
	            	                    				value = fn + ' is null ';
	            	                    			} else {
	            	                    				value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
	            	                    			}
	             	                    		} else {
	             	                    			if (value == '-无-') {
	            	                    				value = 'nvl(to_char(' + fn + '),\' \')=\' \'';
	            	                    			} else {
	            	                    				if(value)value=value.replace(/\'/g,"''");
	            	                    				value = fn + " LIKE '" + value + "%' ";
	            	                    			}
	             	                    		}
	    	                    			}
	    	                    		} else if(f.xtype == 'datefield') {
	    	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
	    	                    		} else if(f.column && f.column.xtype == 'numbercolumn'){
	    	                    			if(f.column.format) {
	    	                    				var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
	    	                    				//防止to_char去除小数点前面的0
	    	                    				if(-1<value&&value<1){
		    	                    				var number = value;
		    	                    				value = "to_char(round(" + fn + "," + precision + "),";	    	                    		
		    	                    				value += "'fm0.";
		    	                    				for(var i=0;i<precision;i++){
		    	                    					value += "0";
		    	                    				}
		    	                    				value += "') like '%" + number + "%' ";
		    	                    			}else{
		    	                    				value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
		    	                    			}
	    	                    			} else
	    	                    				value = "to_char(" + fn + ") like '%" + value + "%' ";
	    	                    		} else {
	    	                    			/**字符串转换下简体*/
	    	                    			if(value)value=value.replace(/\'/g,"''");
	    	                    			var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
	    	                    			//可能就是按繁体筛选  
	    	                    			if(f.ignoreCase) {// 忽略大小写
	        	                    			fn = 'upper(' + fn + ')';
	        	                    			value = value.toUpperCase();
	        	                    		}
	        	                    		if(!f.autoDim) {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    			
	        	                    			
	        	                    		} else if(f.filterSelect||f.inputEl.dom.disabled||(f.rawValue==''&&f.emptyText==value)){
		        	                    		if(f.filterType == 'direct'){
		        	                    			value=fn+"='"+value+"'";
		        	                    		} else if(f.filterType == 'nodirect'){
		        	                    			value="nvl("+fn+",' ')<>'"+value+"'";
		        	                    		} else if(f.filterType == 'head'){
		        	                    			value = fn + " LIKE '" + value + "%' ";
		        	                    		} else if(f.filterType == 'end'){
		        	                    			value = fn + " LIKE '%" + value + "' ";
		        	                    		} else if(f.filterType == 'null'){
		        	                    			value = fn + " is null";
		        	                    		} else if(f.filterType == 'novague'){
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " not LIKE '%" + value + "%' and "+fn+" not LIKE '%"+SimplizedValue+"%' or "+fn+" is null)";
		        	                    			}else value = "("+fn + " not LIKE '%" + value + "%' or "+fn+" is null)";
		        	                    		} else{
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
		        	                    			}else value = fn + " LIKE '%" + value + "%' ";
		        	                    			f.filterType = '';
		        	                    		}
		        	                    		f.filterSelect = false;
	        	                    		}else {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '%" + value + "%' ";
	        	                    			f.filterType = '';
	        	                    		}
	    	                    		}
	    	                    	}
	                        	}
                    	}else value ="nvl("+fn+",' ')=' '";
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = condition;
               /* var param = {caller: caller, condition: condition,_config:getUrlParam('_config'),page:1,pageSize:pageSize};*/
                grid.getData(grid,getUrlParam('whoami'),condition,1,pageSize,-1);
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    setLoading : function(b) {
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
 }
});