package me.camdenorrb.jcommons.base.tryblock;

/**
 * Represents a try block with a return type
 *
 * @param <T> The type that is expected to be returned
 */
@FunctionalInterface
public interface SupplierTryBlock<T> {

	/**
	 * Attempt to run the method and return a value
	 *
	 * @return The attempted value
	 *
	 * @throws Exception In order to be catched elsewhere
	 */
	T attempt() throws Exception;

}