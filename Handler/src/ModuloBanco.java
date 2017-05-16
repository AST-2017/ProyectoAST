

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

public class ModuloBanco implements Module {
	public void init(ConfigurationContext configContext, AxisModule module) throws AxisFault {
	}

	public void engageNotify(AxisDescription axisDescription) throws AxisFault {
	}

	public void shutdown(ConfigurationContext configurationContext) throws AxisFault {
	}

	public String[] getPolicyNamespaces() {
		return null;
	}

	public void applyPolicy(Policy policy, AxisDescription axisDescription) throws AxisFault {
	}

	public boolean canSupportAssertion(Assertion assertion) {
		return true;
	}
}
