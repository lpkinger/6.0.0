var Util={
		getCount: function(c, d){
			c = c || caller;
			d = d || condition;
			var me = this;
			var f = d;
			if(me.filterCondition){
				if(d == null || d == ''){
					f = me.filterCondition;
				} else {
					f += ' AND ' + me.filterCondition;
				}
			}
			Ext.Ajax.request({//拿到grid的数据总数count
	        	url : basePath + '/common/datalistCount.action',
	        	params: {
	        		caller: c,
	        		condition: f
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        		
	        			return;
	        		}
	        		dataCount = res.count;
	        		Util.getColumnsAndStore(c, d);
	        	}
	        });
		},
		getColumnsAndStore: function(c, d, g, s){
			c = c || caller;
			d = d || condition;
			g = g || page;
			s = s || pageSize;
			var me = this;
			var f = d;
			if(me.filterCondition){
				if(d == null || d == ''){
					f = me.filterCondition;
				} else {
					f += ' AND ' + me.filterCondition;
				}
			}
			//me.BaseUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'common/datalist.action',
	        	params: {
	        		caller: c,
	        		condition:  f, 
	        		page: g,
	        		pageSize: s
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		//me.BaseUtil.getActiveTab().setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			
	        			return;
	        		}
	        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
	        		if(me.columns && me.columns.length > 2){
	        			me.store.loadData(data);
	        			if(me.store.data.items.length != data.length){
	        				me.store.add(data);
	        			}
	        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
	            			Ext.each(me.store.data.items, function(item){
	            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
	            					me.selModel.select(item);
	            				}
	            			});
	            		}
	        		} else {
	        			var store = Ext.create('Ext.data.Store', {
	            		    fields: res.fields,
	            		    data: data
	            		});
	    				//处理render
	        			var grid = this;
	                    Ext.Array.each(res.columns, function(column, y) {   
	        				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
	        					if(!grid.RenderUtil){
	        						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
	        					}
	                    		var renderName = column.renderer;
	                    		if(contains(column.renderer, ':', true)){
	                    			var args = new Array();
	                    			Ext.each(column.renderer.split(':'), function(a, index){
	                    				if(index == 0){
	                    					renderName = a;
	                    				} else {
	                    					args.push(a);
	                    				}
	                    			});
	                    			if(!grid.RenderUtil.args[renderName]){
	                    				grid.RenderUtil.args[renderName] = new Object();
	                    			}
	                    			grid.RenderUtil.args[renderName][column.dataIndex] = args;
	                    			//这里只能用column.dataIndex来标志，不能用x,y,index等，
	                    			//grid在render时，checkbox占一列
	                    		}
	                    		column.renderer = grid.RenderUtil[renderName];
	                    		column.haveRendered = true;
	                    	}
	                    });            
	            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
	        		}
	        		//修改pagingtoolbar信息
	        		Ext.getCmp('pagingtoolbar').afterOnLoad();
	        		//拿到datalist对应的单表的关键词
	        		keyField = res.keyField;//form表主键字段
	        		pfField = res.pfField;//grid表主键字段
	        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
	        		relative = res.relative;
	        		if(res.vastbutton && res.vastbutton == 'erpAddButton'){//[新增]功能
	        			Ext.getCmp('erpAddButton').show();
	        		}
	        	}
	        });
		},
};