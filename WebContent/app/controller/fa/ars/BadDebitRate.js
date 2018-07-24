Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.BadDebitRate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.grid.Panel4','core.toolbar.Toolbar3','core.button.Save'
          	
        	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel4': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeUpdate();

    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClickForEditGrid(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	
	beforeUpdate: function(){

		var mm = this;
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(grids.length > 0){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				var msg = grid.GridUtil.checkGridDirty(grid);
				if(msg.length > 0){
					s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
				}
			});
		}
			var params = [];
			
			var rate = 0;
			var griddata = grids[0].store.data.items;
			
			if(griddata.length > 0){
				
				Ext.each(griddata,function(data,index){
					var rate1 =Ext.Number.from(data.data.bdr_rate,0);
					rate = rate+rate1;
				});
				
				if(rate!=100){
					warnMsg('比率合计不为100%,是否继续保存?',function(btn){
						if(btn=='yes'){
							var param = grids[0].GridUtil.getGridStore();
						    params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
						    mm.update(params);
						}else{
							return;
						}
						
					});
				}else{
					
					var param = grids[0].GridUtil.getGridStore();
					
					if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
						warnMsg('明细表还未添加数据,是否继续?', function(btn){
							if(btn == 'yes'){
								params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
								mm.update(params);
							}else{
								return;
							}
							
						});
					}else{
						params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
						mm.update(params);
					}
				    
				}
				
				
			}
			
			
			
			
			
//			if(grids.length > 0){
//				var param = grids[0].GridUtil.getGridStore();
//				
//				if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
//					warnMsg('明细表还未添加数据,是否继续?', function(btn){
//						if(btn == 'yes'){
//							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
//						} else {
//							return;
//						}
//					});
//				} else {
//					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
//				}
//			}
//			mm.update(params);

		/*
		Array.prototype.contains = function(obj) {
		    var i = this.length;
		    while (i--) {
		        if (this[i] === obj) {
		            return true;
		        }
		    }
		    return false;
		};
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var rowNo = [];
		Ext.each(items,function(item,index){
			Ext.each(grid.columns, function(c){
				if(item.data[c.dataIndex]!=item.raw[c.dataIndex])
					{
						if(!rowNo.contains(index+1)){
						
							rowNo.push(index+1);
						}
					
					}
				});
		 	});
			if(rowNo.length==0){
				Ext.Msg.alert("你未对数据做任何修改!");
				return;
			} else {
				var result = confirm("第"+rowNo.toString()+"行已经修改,确定更新?")
				if(result){
					var index = 0;
					var jsonGridData = new Array();
					var s = grid.getStore().data.items;
					for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
						var data = s[i].data;
							Ext.each(grid.columns, function(c){
							if(c.xtype == 'datecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
								} else {
									data[c.dataIndex] = '1970-01-01';//如果用户没输入日期，或输入有误，就给个默认日期，
									//或干脆return；并且提示一下用户
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
									data[c.dataIndex] = '0';
								}
							}
						});
						jsonGridData[index++] = Ext.JSON.encode(data);
					}
					this.update(jsonGridData.toString());
				}
				else return;
			}
		
	*/},
	update:function(param){

		var params = new Object();
		params.param = unescape(arguments[0].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		var mm = this;

		Ext.Ajax.request({
	   		url : basePath+'fa/ars/updateBadDebitRate.action',
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				
    				updateSuccess(function(btn){
    					//update成功后刷新页面进入可编辑的页面 
    					var url = window.location.href;
		   		    	window.location.href = url;
	   				});
	   			} else if(localJson.exceptionInfo){} else {
	   				updateFailure();
	   			}
	   		}
		});
	
	/*
		Ext.Ajax.request({
		    url: basePath+'fa/ars/updateBadDebitRate.action',
		    params:{
		    	param:param
		    },
		    success: function(response){
		        var text = response.responseText;
		        result = Ext.decode(text);
		        if(result.success){
		        	Ext.Msg.alert("保存成功！"); // 尚未国际化，以后订正。
		        }
		    }
		});
		
	*/},

    /**
	 * string:原始字符串
	 * substr:子字符串
	 * isIgnoreCase:忽略大小写
	 */
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	}
});