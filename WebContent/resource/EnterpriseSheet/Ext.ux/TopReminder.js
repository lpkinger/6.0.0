/**
 * EnterpriseSheet 
 * Copyright (c) FeyaSoft Inc 2014. All right reserved.
 * http://www.enterpriseSheet.com
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY,FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
Ext.define('Ext.ux.TopReminder', {
	
	singleton: true,
	
	createBox : function(t, s){		
		return '<div class="top-reminder">'+
			(t ? '<h3>' + t + '</h3>' : '')+
			'<p>' + s + '</p></div>';
	},
	
	showMsg : function(title, msg, stayTime){
        if(!this.msgCt){
            this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'top-reminder'}, true);
        }	            
        var m = Ext.DomHelper.append(this.msgCt, this.createBox(title, msg), true);
        
        m.hide();        
        
        stayTime = stayTime || 4000;
        m.slideIn('t').ghost("t", { delay: stayTime, remove: true});
    },

    init : function(){
        if(!this.msgCt){
            // It's better to create the msg-div here in order to avoid re-layouts 
            // later that could interfere with the HtmlEditor and reset its iFrame.
        	this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'top-reminder'}, true);
        }
    }		
}, function(){
	TOPMINDER = Ext.ux.TopReminder;
	Ext.onReady(TOPMINDER.init, TOPMINDER);
});
