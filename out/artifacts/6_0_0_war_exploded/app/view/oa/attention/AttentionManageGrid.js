Ext.define('erp.view.oa.attention.AttentionManageGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAttentionManageGridPanel',
	id: 'AttentionGridPanel', 
	layout : 'fit',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
     /** selModel: Ext.create('Ext.selection.CheckboxModel',{
    	ignoreRightMouseSelection : false,
    	injectCheckbox:1,
		listeners:{
            selectionchange:function(selectionModel, selected, options){
            }
        },
        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
        	var me = Ext.getCmp('AttentionGridPanel');
        	console.log(index);
        	var bool = true;
        	var items = me.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(item.data == record.data){
            		bool = false;
            		me.selModel.deselect(record);
            		Ext.Array.remove(items, item);
            		Ext.Array.remove(me.multiselected, record);
            	}
            });
            Ext.each(me.multiselected, function(item, index){
            	items.push(item);
            });
            me.selModel.select(items);
        	if(bool){
        	  view.el.focus();
              var checkbox = item.childNodes[1].childNodes[0].childNodes[0];
	        	if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
	        		me.multiselected.push(record);
	        		//多选
	        		var datas=me.store.data.items;
	        		for(var i=index;i<datas.length;i++){
	        		 if(datas[i].data.ap_attentedemid==record.data.ap_attentedemid){
	        		 items.push(datas[i]);
	        		 }else if(datas[i].data.ap_attentedemid!=record.data.ap_attentedemid) break;
	        		}	        		
	        		items.push(record);
	        		me.selModel.select(items);
	        	} else {
	        		me.selModel.deselect(record);
	        		Ext.Array.remove(me.multiselected, record);
	        	}
        	}
        },
        onHeaderClick: function(headerCt, header, e) {
        	if (header.isCheckerHd) {
                e.stopEvent();
                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                if (isChecked) {
                    this.deselectAll(true);
                    var grid = Ext.getCmp('AttentionGridPanel');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                   header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                } else {
                header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                	var grid = Ext.getCmp('AttentionGridPanel');
                	this.deselect(grid.multiselected);
	                grid.multiselected = new Array();
	                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                    this.selectAll(true);
                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                }
            }
        }
	}),
	**/
	initComponent : function(){ 
	    this.addEvents({
         mouseover: true
        });
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "1=1": condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: "AttentionManage", condition: condition,page: page,pageSize: pageSize};
    	this.getGridColumnsAndStore(this, 'oa/attention/getAttentionDataAndColumns.action?', gridParam, "");
    	this.callParent(arguments); 
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: true,
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			Ext.each(res.columns, function(column, y){
        				//render
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
                    		}
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    	}
                    });
                    }
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			me.add10EmptyData(grid.detno, data);
            			me.add10EmptyData(grid.detno, data);//添20条吧
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			//me.add10EmptyData(grid.detno, data);
            		}
            		var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data,    		   
            		});
            		
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		
            		grid.reconfigure(store, res.columns); 
        		    //grid.mergeCells(grid,[1,2,3,4]);
                     /**setTimeout(function(){
                   //  grid.mergeCells(grid,[1,2,3,4]);
                    },10000);**/
            		}
        });
	},
	viewConfig: {
        stripeRows: true
    },
    	getMultiSelected: function(){
		var grid = this;
        var items = grid.selModel.getSelection();
		var records=Ext.Array.unique(grid.multiselected);
	      	var params = new Object();
			params.caller = caller;
			var data = new Array();
			Ext.each(records, function(record, index){
					var o = new Object();
                     o.ap_attentedemid = record.data.ap_attentedemid;
					data.push(o);
			});
	        params.data = Ext.encode(data);
	        return params;
	},
	loadNewStore: function(grid, param){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/attention/getAttentionDataAndColumns.action",
        	params: param,
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			me.add10EmptyData(grid.detno, data);
        			me.add10EmptyData(grid.detno, data);//添20条吧
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));        			
        		}
        		grid.store.loadData(data);
        		grid.mergeCells(grid,[1,2,3,4]);
        	}
        });
	},
	/**
	 * 从index行开始，往grid里面加十空行
	 * @param detno 编号字段
	 * @param data 需要添加空白数据的data
	 */
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
	 getSearchValue: function() {
        var me = this,
            value = Ext.getCmp('search').getValue();
            
        if (value === '') {
            return null;
        }
        if (!me.regExpMode) {
            value = value.replace(me.regExpProtect, function(m) {
                return '\\' + m;
            });
        } else {
            try {
                new RegExp(value);
            } catch (error) {
                me.statusBar.setStatus({
                    text: error.message,
                    iconCls: 'x-status-error'
                });
                return null;
            }
            // this is stupid
            if (value === '^' || value === '$') {
                return null;
            }
        }

        return value;
    },
    mergeCells:function(grid,cols){  
    var arrayTr=document.getElementById(grid.getId()+"-body").firstChild.firstChild.firstChild.getElementsByTagName('tr');    
    var trCount = arrayTr.length;  
    var arrayTd;  
    var td;  
    var merge = function(rowspanObj,removeObjs){ //定义合并函数  
        if(rowspanObj.rowspan != 1){  
            arrayTd =arrayTr[rowspanObj.tr].getElementsByTagName("td"); //合并行  
            td=arrayTd[rowspanObj.td-1];  
            td.rowSpan=rowspanObj.rowspan;  
            td.vAlign="middle";
            td.style="text-align:center";               
            Ext.each(removeObjs,function(obj){ //隐身被合并的单元格  
                arrayTd =arrayTr[obj.tr].getElementsByTagName("td");  
                arrayTd[obj.td-1].style.display='none';                           
            });  
        }     
    };    
    var rowspanObj = {}; //要进行跨列操作的td对象{tr:1,td:2,rowspan:5}      
    var removeObjs = []; //要进行删除的td对象[{tr:2,td:2},{tr:3,td:2}]  
    var col;  
    Ext.each(cols,function(colIndex){ //逐列去操作tr  
        var rowspan = 1;  
        var divHtml = null;         
        for(var i=1;i<trCount;i++){
            arrayTd = arrayTr[i].getElementsByTagName("td");  
            var cold=0;  
//          Ext.each(arrayTd,function(Td){ //获取RowNumber列和check列  
//              if(Td.getAttribute("class").indexOf("x-grid-cell-special") != -1)  
//                  cold++;                               
//          });  
            col=colIndex+cold;//跳过RowNumber列和check列  
            if(!divHtml){  
                divHtml = arrayTd[col-1].innerHTML;  
                rowspanObj = {tr:i,td:col,rowspan:rowspan}  
            }else{  
                var cellText = arrayTd[col-1].innerHTML;  
                var addf=function(){   
                    rowspanObj["rowspan"] = rowspanObj["rowspan"]+1;  
                    removeObjs.push({tr:i,td:col});  
                    if(i==trCount-1)  
                        merge(rowspanObj,removeObjs);
                };  
                var mergef=function(){  
                    merge(rowspanObj,removeObjs);
                    divHtml = cellText;  
                    rowspanObj = {tr:i,td:col,rowspan:rowspan}  
                    removeObjs = [];  
                };  
                if(cellText == divHtml){  
                    if(colIndex!=cols[0]){   
                        var leftDisplay=arrayTd[col-2].style.display;
                        if(leftDisplay=='none')  
                            addf();   
                        else  
                            mergef();                             
                    }else  
                        addf();                                           
                }else  
                    mergef();             
            }  
        }  
    });   
}  
});